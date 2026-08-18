#!/usr/bin/env python3
"""
ScarfBench candidate-repository scraper.

Discovers public GitHub repositories that look like valid ScarfBench task candidates
per the paper's app-selection criteria (framework migration benchmark):

  1. Uses a target web framework (Spring Boot / Quarkus / Jakarta EE / Micronaut / Helidon / Vert.x
     or Flask / FastAPI / Django or Express / Fastify / NestJS or Axum / Actix / Rocket).
  2. Small-to-medium codebase (100 – 10,000 LoC of source).
  3. Has HTTP routes (real web app, not a library).
  4. Has a build system (pom.xml / Cargo.toml / package.json / requirements.txt / setup.py).
  5. Has tests (test/ directory or *_test / *.test / test_* files).
  6. Optional: Dockerfile / docker-compose for reproducibility.
  7. Non-trivial recent activity (commits within last N months) OR stable-mature (500+ stars).
  8. Non-fork, permissive license, not archived.

For each candidate, the script:
  - Clones a shallow copy
  - Runs a per-language checklist scorer (routes count, tests present, LoC, framework detected)
  - Emits a candidates.json report ranked by fitness score
  - Optionally attaches a per-app "layer" heuristic: business_domain / dependency_injection /
    infrastructure / persistence / presentation / whole_applications (matching the
    ScarfBench 6-layer taxonomy)

Requires:
  - GITHUB_TOKEN env var (personal access token, `public_repo` scope) — for higher rate limits
  - `git` in PATH
  - Python 3.10+, `requests`

Usage:
  export GITHUB_TOKEN=ghp_xxxxx
  python scripts/scarfbench_repo_scraper.py \
      --framework spring \
      --max-repos 40 \
      --out candidates_spring.json \
      --clone-dir ./candidate_repos

  # Multiple frameworks (parallel):
  python scripts/scarfbench_repo_scraper.py \
      --framework spring quarkus jakarta \
      --max-repos 20 \
      --out candidates_java.json

  # Skip cloning (only fetch metadata + shallow analysis via GitHub API):
  python scripts/scarfbench_repo_scraper.py --framework spring --no-clone --out first_pass.json
"""

from __future__ import annotations

import argparse
import concurrent.futures
import json
import logging
import os
import shutil
import subprocess
import sys
import time
from collections import Counter
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Iterable

import requests

LOG = logging.getLogger("scarfbench-scraper")

# -----------------------------------------------------------------------------
# Framework catalogue — GitHub search filters + per-framework file heuristics
# -----------------------------------------------------------------------------
FRAMEWORK_SPEC: dict[str, dict] = {
    # --------- Java ---------
    "spring": {
        "language": "Java",
        "search_terms": [
            'language:Java "org.springframework.boot" "@RestController"',
            'language:Java "spring-boot-starter-web" filename:pom.xml',
        ],
        "route_markers": ["@RestController", "@Controller", "@GetMapping", "@PostMapping", "@RequestMapping"],
        "framework_markers": ["spring-boot", "org.springframework"],
        "build_files": ["pom.xml", "build.gradle", "build.gradle.kts"],
    },
    "quarkus": {
        "language": "Java",
        "search_terms": [
            'language:Java "io.quarkus" filename:pom.xml',
            'language:Java "@Path" "@ApplicationScoped" quarkus',
        ],
        "route_markers": ["@Path", "@GET", "@POST"],
        "framework_markers": ["io.quarkus", "quarkus-resteasy", "quarkus-arc"],
        "build_files": ["pom.xml", "build.gradle"],
    },
    "jakarta": {
        "language": "Java",
        "search_terms": [
            'language:Java "jakarta.ws.rs" "@Path"',
            'language:Java "javax.ws.rs" "@Path" filename:pom.xml',
        ],
        "route_markers": ["@Path", "@GET", "@POST", "@PUT", "@DELETE"],
        "framework_markers": ["jakarta.ws.rs", "javax.ws.rs", "jakarta.ee"],
        "build_files": ["pom.xml"],
    },
    "micronaut": {
        "language": "Java",
        "search_terms": ['language:Java "io.micronaut" "@Controller" filename:pom.xml'],
        "route_markers": ["@Controller", "@Get", "@Post"],
        "framework_markers": ["io.micronaut"],
        "build_files": ["pom.xml", "build.gradle"],
    },
    "helidon": {
        "language": "Java",
        "search_terms": ['language:Java "io.helidon" filename:pom.xml'],
        "route_markers": ["@Path", "Routing.builder", "@GET"],
        "framework_markers": ["io.helidon"],
        "build_files": ["pom.xml"],
    },
    "vertx": {
        "language": "Java",
        "search_terms": ['language:Java "io.vertx.core.Vertx" filename:pom.xml'],
        "route_markers": ["Router.router", ".handler(", ".route("],
        "framework_markers": ["io.vertx"],
        "build_files": ["pom.xml", "build.gradle"],
    },
    # --------- Python ---------
    "flask": {
        "language": "Python",
        "search_terms": ['language:Python "from flask import" "@app.route"'],
        "route_markers": ["@app.route", "@blueprint.route"],
        "framework_markers": ["from flask", "flask.Flask"],
        "build_files": ["requirements.txt", "pyproject.toml", "setup.py", "Pipfile"],
    },
    "fastapi": {
        "language": "Python",
        "search_terms": ['language:Python "from fastapi import" "@app.get"'],
        "route_markers": ["@app.get", "@app.post", "@router.get", "@router.post"],
        "framework_markers": ["from fastapi", "FastAPI("],
        "build_files": ["requirements.txt", "pyproject.toml"],
    },
    "django": {
        "language": "Python",
        "search_terms": ['language:Python "from django.urls import path" filename:urls.py'],
        "route_markers": ["path(", "url(", "re_path("],
        "framework_markers": ["from django", "django.conf"],
        "build_files": ["requirements.txt", "pyproject.toml", "manage.py"],
    },
    # --------- TypeScript / Node ---------
    "express": {
        "language": "TypeScript",
        "search_terms": [
            'language:TypeScript "import express from" "app.get("',
            'language:JavaScript "require(\'express\')" "app.get(" package.json',
        ],
        "route_markers": ["app.get(", "app.post(", "router.get(", "router.post("],
        "framework_markers": ["express", "\"express\":"],
        "build_files": ["package.json"],
    },
    "fastify": {
        "language": "TypeScript",
        "search_terms": ['language:TypeScript "fastify" "@fastify" package.json'],
        "route_markers": ["fastify.get(", "fastify.post(", ".route({"],
        "framework_markers": ["fastify"],
        "build_files": ["package.json"],
    },
    "nestjs": {
        "language": "TypeScript",
        "search_terms": ['language:TypeScript "@nestjs/common" "@Controller"'],
        "route_markers": ["@Controller(", "@Get(", "@Post("],
        "framework_markers": ["@nestjs/common", "@nestjs/core"],
        "build_files": ["package.json"],
    },
    # --------- Rust ---------
    "axum": {
        "language": "Rust",
        "search_terms": ['language:Rust "use axum::" "Router::new()"'],
        "route_markers": ["Router::new()", ".route(", "get(", "post("],
        "framework_markers": ["axum", "\"axum\""],
        "build_files": ["Cargo.toml"],
    },
    "actix": {
        "language": "Rust",
        "search_terms": ['language:Rust "actix_web" "HttpServer::new"'],
        "route_markers": ["HttpServer::new", "web::get()", "web::post()", "web::resource"],
        "framework_markers": ["actix-web", "actix_web"],
        "build_files": ["Cargo.toml"],
    },
    "rocket": {
        "language": "Rust",
        "search_terms": ['language:Rust "#[macro_use] extern crate rocket" "#[get("'],
        "route_markers": ["#[get(", "#[post(", "#[put(", "#[delete("],
        "framework_markers": ["rocket", "#[macro_use] extern crate rocket"],
        "build_files": ["Cargo.toml"],
    },
}

# Layer taxonomy — used to bucket candidates like the ScarfBench 6-layer split.
# Keyword hits in README/description → layer suggestion.
LAYER_KEYWORDS = {
    "business_domain":        ["business", "domain", "logic", "service", "workflow"],
    "dependency_injection":   ["di", "injection", "bean", "component", "@Inject"],
    "infrastructure":         ["async", "scheduler", "timer", "job", "queue", "cron"],
    "persistence":            ["jpa", "hibernate", "orm", "database", "repository", "sqlx", "diesel"],
    "presentation":           ["controller", "endpoint", "route", "restcontroller", "webflux", "template", "jsp"],
    "whole_applications":     ["petclinic", "conduit", "realworld", "todo", "blog", "shop", "erp"],
}

GITHUB_API = "https://api.github.com"

# -----------------------------------------------------------------------------
# Data classes
# -----------------------------------------------------------------------------
@dataclass
class Candidate:
    full_name: str
    html_url: str
    clone_url: str
    default_branch: str
    stars: int
    forks: int
    pushed_at: str
    size_kb: int
    license_spdx: str | None
    is_fork: bool
    archived: bool
    description: str | None
    topics: list[str] = field(default_factory=list)

    # Post-clone analysis
    framework: str = ""
    loc: int = 0
    route_count: int = 0
    test_files: int = 0
    build_files_found: list[str] = field(default_factory=list)
    has_dockerfile: bool = False
    has_readme: bool = False
    detected_layer: str | None = None

    fitness_score: float = 0.0
    reasons: list[str] = field(default_factory=list)


# -----------------------------------------------------------------------------
# GitHub API helpers
# -----------------------------------------------------------------------------
def gh_headers() -> dict[str, str]:
    tok = os.environ.get("GITHUB_TOKEN", "").strip()
    hdrs = {"Accept": "application/vnd.github+json", "X-GitHub-Api-Version": "2022-11-28"}
    if tok:
        hdrs["Authorization"] = f"Bearer {tok}"
    else:
        LOG.warning("GITHUB_TOKEN not set — 60 req/hr rate limit applies. Set it for 5000 req/hr.")
    return hdrs


def gh_search(query: str, per_page: int = 30, max_results: int = 60) -> Iterable[dict]:
    """Yield search-code results, honouring rate limits."""
    page = 1
    while True:
        url = f"{GITHUB_API}/search/code"
        params = {"q": query, "per_page": min(per_page, 100), "page": page}
        r = requests.get(url, headers=gh_headers(), params=params, timeout=30)
        remaining = int(r.headers.get("x-ratelimit-remaining", "0"))
        if r.status_code == 403 and "rate limit" in r.text.lower():
            reset = int(r.headers.get("x-ratelimit-reset", str(int(time.time()) + 60)))
            wait = max(1, reset - int(time.time()) + 1)
            LOG.warning("Rate-limited. Sleeping %ds…", wait)
            time.sleep(wait)
            continue
        r.raise_for_status()
        data = r.json()
        items = data.get("items", [])
        if not items:
            return
        for item in items:
            yield item
        if len(items) < per_page or page * per_page >= max_results:
            return
        page += 1
        if remaining < 5:
            time.sleep(2)


def gh_repo(full_name: str) -> dict:
    r = requests.get(f"{GITHUB_API}/repos/{full_name}", headers=gh_headers(), timeout=30)
    r.raise_for_status()
    return r.json()


# -----------------------------------------------------------------------------
# Repo cloning + local analysis
# -----------------------------------------------------------------------------
def clone_shallow(clone_url: str, dst: Path) -> bool:
    if dst.exists():
        return True
    dst.parent.mkdir(parents=True, exist_ok=True)
    try:
        subprocess.run(
            ["git", "clone", "--depth", "1", "--single-branch", clone_url, str(dst)],
            check=True, capture_output=True, timeout=180,
        )
        return True
    except subprocess.CalledProcessError as e:
        LOG.warning("clone failed: %s (%s)", clone_url, e.stderr.decode()[:200])
        return False
    except subprocess.TimeoutExpired:
        LOG.warning("clone timeout: %s", clone_url)
        return False


def analyze_repo(repo_dir: Path, framework: str) -> dict:
    """Walk the cloned repo and return counts + markers."""
    spec = FRAMEWORK_SPEC[framework]
    stats = {
        "loc": 0,
        "route_count": 0,
        "test_files": 0,
        "build_files_found": [],
        "has_dockerfile": False,
        "has_readme": False,
        "detected_layer": None,
    }
    source_exts = {".java", ".py", ".ts", ".tsx", ".js", ".rs"}

    layer_hits = Counter()
    keyword_pool = " ".join(w for lst in LAYER_KEYWORDS.values() for w in lst).lower()

    for path in repo_dir.rglob("*"):
        if not path.is_file():
            continue
        name = path.name
        low = name.lower()
        rel = str(path.relative_to(repo_dir))
        # Skip vendor/build dirs
        parts_lower = {p.lower() for p in path.parts}
        if parts_lower & {"node_modules", "target", ".venv", "venv", "__pycache__", "build", "dist", ".git"}:
            continue

        if name in spec["build_files"]:
            stats["build_files_found"].append(rel)
        if low == "dockerfile" or low.startswith("dockerfile."):
            stats["has_dockerfile"] = True
        if low.startswith("readme"):
            stats["has_readme"] = True
        if "test" in low or "spec" in low:
            if path.suffix in source_exts:
                stats["test_files"] += 1

        if path.suffix in source_exts:
            try:
                with path.open("r", encoding="utf-8", errors="ignore") as f:
                    for line in f:
                        stats["loc"] += 1
                        for marker in spec["route_markers"]:
                            if marker in line:
                                stats["route_count"] += 1
                                break
                        low_line = line.lower()
                        for layer, kws in LAYER_KEYWORDS.items():
                            for kw in kws:
                                if kw.lower() in low_line:
                                    layer_hits[layer] += 1
            except Exception:
                pass

    if layer_hits:
        stats["detected_layer"] = layer_hits.most_common(1)[0][0]
    return stats


def score(c: Candidate) -> float:
    """Compute a fitness score in [0, 100] for benchmark-suitability."""
    s = 0.0
    reasons: list[str] = []
    # LoC gate
    if 100 <= c.loc <= 10000:
        s += 25
        reasons.append(f"LoC={c.loc} in target range (100-10000)")
    elif c.loc > 10000:
        reasons.append(f"LoC={c.loc} too large (>10000)")
        s += 5
    else:
        reasons.append(f"LoC={c.loc} tiny (<100)")
    # Routes
    if c.route_count >= 3:
        s += 25
        reasons.append(f"{c.route_count} HTTP routes detected")
    elif c.route_count >= 1:
        s += 12
        reasons.append(f"{c.route_count} route detected")
    # Tests
    if c.test_files >= 3:
        s += 15
        reasons.append(f"{c.test_files} test files")
    elif c.test_files >= 1:
        s += 7
        reasons.append(f"{c.test_files} test file")
    # Build files
    if c.build_files_found:
        s += 10
        reasons.append(f"build files: {c.build_files_found[:3]}")
    # Dockerfile
    if c.has_dockerfile:
        s += 5
        reasons.append("Dockerfile present")
    # README
    if c.has_readme:
        s += 3
        reasons.append("README present")
    # Popularity
    if c.stars >= 500:
        s += 10
        reasons.append(f"{c.stars} stars (mature)")
    elif c.stars >= 50:
        s += 5
        reasons.append(f"{c.stars} stars")
    # License
    if c.license_spdx and c.license_spdx.lower() in {"mit", "apache-2.0", "bsd-3-clause", "bsd-2-clause"}:
        s += 5
        reasons.append(f"permissive license: {c.license_spdx}")
    # Freshness
    if c.pushed_at and c.pushed_at >= "2024":
        s += 2
        reasons.append(f"recently pushed ({c.pushed_at[:10]})")
    # Penalties
    if c.is_fork:
        s -= 20
        reasons.append("PENALTY: is a fork")
    if c.archived:
        s -= 30
        reasons.append("PENALTY: archived")

    c.reasons = reasons
    return round(max(0.0, min(100.0, s)), 1)


# -----------------------------------------------------------------------------
# Main pipeline
# -----------------------------------------------------------------------------
def discover(framework: str, max_repos: int) -> list[dict]:
    spec = FRAMEWORK_SPEC[framework]
    seen: set[str] = set()
    items: list[dict] = []
    for query in spec["search_terms"]:
        for hit in gh_search(query, max_results=max_repos * 2):
            full = hit.get("repository", {}).get("full_name")
            if not full or full in seen:
                continue
            seen.add(full)
            items.append(hit["repository"])
            if len(items) >= max_repos:
                return items
    return items


def enrich_and_analyze(
    repo_meta: dict, framework: str, clone_dir: Path, do_clone: bool
) -> Candidate | None:
    try:
        detail = gh_repo(repo_meta["full_name"])
    except requests.HTTPError as e:
        LOG.warning("skip %s: %s", repo_meta["full_name"], e)
        return None

    lic = (detail.get("license") or {}).get("spdx_id")
    c = Candidate(
        full_name=detail["full_name"],
        html_url=detail["html_url"],
        clone_url=detail["clone_url"],
        default_branch=detail["default_branch"],
        stars=detail.get("stargazers_count", 0),
        forks=detail.get("forks_count", 0),
        pushed_at=detail.get("pushed_at", ""),
        size_kb=detail.get("size", 0),
        license_spdx=lic,
        is_fork=detail.get("fork", False),
        archived=detail.get("archived", False),
        description=detail.get("description"),
        topics=detail.get("topics", []),
        framework=framework,
    )

    if do_clone and not c.is_fork and not c.archived:
        slug = c.full_name.replace("/", "__")
        dst = clone_dir / slug
        if clone_shallow(c.clone_url, dst):
            stats = analyze_repo(dst, framework)
            for k, v in stats.items():
                setattr(c, k, v)

    c.fitness_score = score(c)
    return c


def run(args):
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    clone_dir = Path(args.clone_dir).expanduser().resolve()
    out_path = Path(args.out).expanduser().resolve()

    all_candidates: list[Candidate] = []
    for fw in args.framework:
        if fw not in FRAMEWORK_SPEC:
            LOG.error("unknown framework: %s (choose from %s)", fw, list(FRAMEWORK_SPEC))
            continue
        LOG.info("=== Discovering repos for framework=%s ===", fw)
        repos = discover(fw, args.max_repos)
        LOG.info("  %d unique repos found; enriching + analyzing", len(repos))
        with concurrent.futures.ThreadPoolExecutor(max_workers=args.concurrency) as ex:
            futures = {
                ex.submit(enrich_and_analyze, r, fw, clone_dir, not args.no_clone): r
                for r in repos
            }
            for fut in concurrent.futures.as_completed(futures):
                c = fut.result()
                if c:
                    all_candidates.append(c)
                    LOG.info("  %-45s score=%5.1f  routes=%3d  tests=%3d  loc=%6d  %s",
                             c.full_name, c.fitness_score, c.route_count,
                             c.test_files, c.loc, "★" * (1 if c.stars > 500 else 0))

    # Sort by score desc
    all_candidates.sort(key=lambda x: -x.fitness_score)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    with out_path.open("w") as f:
        json.dump({
            "generated_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
            "n_candidates": len(all_candidates),
            "candidates": [asdict(c) for c in all_candidates],
        }, f, indent=2)

    LOG.info("=== SUMMARY ===")
    LOG.info("Total candidates: %d", len(all_candidates))
    top10 = all_candidates[:10]
    LOG.info("Top 10 by fitness_score:")
    for c in top10:
        LOG.info("  %5.1f  %-45s  routes=%2d  tests=%2d  loc=%5d",
                 c.fitness_score, c.full_name, c.route_count, c.test_files, c.loc)
    LOG.info("Written: %s", out_path)


def main():
    p = argparse.ArgumentParser(description="ScarfBench candidate-repository scraper")
    p.add_argument("--framework", nargs="+", required=True,
                   choices=list(FRAMEWORK_SPEC),
                   help="Framework(s) to search for. Repeatable.")
    p.add_argument("--max-repos", type=int, default=30,
                   help="Max repos per framework (default 30).")
    p.add_argument("--clone-dir", default="./candidate_repos",
                   help="Where to shallow-clone repos for local analysis.")
    p.add_argument("--out", default="candidates.json",
                   help="Output JSON path for the ranked candidates.")
    p.add_argument("--no-clone", action="store_true",
                   help="Skip cloning; only fetch GitHub API metadata (much faster, less accurate scoring).")
    p.add_argument("--concurrency", type=int, default=4,
                   help="Parallel repo-analysis workers (default 4).")
    args = p.parse_args()
    run(args)


if __name__ == "__main__":
    main()
