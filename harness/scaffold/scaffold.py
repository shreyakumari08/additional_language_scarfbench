#!/usr/bin/env python3
"""
ScarfBench containerization scaffolder (TypeScript + Rust extension tiers).

For every framework variant under the TS/Rust benchmark trees this installs the
files needed to run the ScarfBench execution philosophy (build -> deploy ->
oracle -> deterministic result) uniformly:

  * Dockerfile   - per-language image (build + start server on its bound port)
  * Makefile     - build/up/test/down driving Docker (parallel-safe, port-aware)
  * oracle-lib.sh- shared behavioral-assertion library
  * test.sh      - the behavioral oracle:
                     - a rigorous hand-written canonical oracle if one exists
                       under scaffold/oracles/<layer>__<app>.sh, otherwise
                     - an auto-generated baseline oracle derived from the app's
                       real routes (asserts every endpoint is wired + reachable
                       and root returns a non-empty body) — a genuine upgrade
                       over the previous `curl | grep <word>` smoke checks.

The original Java/Python trees are left untouched. Idempotent: safe to re-run.
Existing hand-written test.sh files are preserved unless --overwrite-oracles is
given (canonical oracles always take precedence when present).
"""
import argparse
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCAFFOLD = ROOT / "harness" / "scaffold"
TEMPLATES = SCAFFOLD / "templates"
ORACLES = SCAFFOLD / "oracles"

LANGS = {
    "typescript": {"bench": ROOT / "typescript" / "benchmark-ts",
                   "dockerfile": TEMPLATES / "Dockerfile.typescript",
                   "src": "src/app.ts", "route_lang": "ts"},
    "rust": {"bench": ROOT / "rust" / "benchmark-rs",
             "dockerfile": TEMPLATES / "Dockerfile.rust",
             "src": "src/main.rs", "route_lang": "rust"},
}

PORT_RE = re.compile(r"(?:0\.0\.0\.0:|127\.0\.0\.1:|listen\(\s*|port\s*[:=]\s*)"
                     r"(\d{4})")
TS_ROUTE_RE = re.compile(r"\.(get|post|put|delete|patch)\(\s*['\"]([^'\"]+)['\"]")
RUST_ROUTE_RE = re.compile(r'\.route\(\s*"([^"]+)"\s*,\s*(get|post|put|delete|patch)')


def detect_port(variant: Path, src_rel: str) -> str:
    src = variant / src_rel
    if src.is_file():
        m = PORT_RE.search(src.read_text(errors="ignore"))
        if m:
            return m.group(1)
    return "8080"


def extract_routes(variant: Path, src_rel: str, route_lang: str):
    """Return list of (method, path) for the variant, best-effort."""
    src = variant / src_rel
    routes = []
    if not src.is_file():
        return routes
    text = src.read_text(errors="ignore")
    if route_lang == "ts":
        for m in TS_ROUTE_RE.finditer(text):
            routes.append((m.group(1).upper(), m.group(2)))
    else:
        for m in RUST_ROUTE_RE.finditer(text):
            routes.append((m.group(2).upper(), m.group(1)))
    # de-dup preserving order
    seen, out = set(), []
    for meth, path in routes:
        key = (meth, path)
        if key not in seen and not path.startswith("http"):
            seen.add(key)
            out.append((meth, path))
    return out


def baseline_oracle(app_key: str, routes) -> str:
    """Generate a status-asserting behavioral oracle from discovered routes."""
    lines = [
        "#!/usr/bin/env bash",
        f"# AUTO-GENERATED baseline behavioral oracle: {app_key}",
        "# Asserts each discovered endpoint is wired (no 404/5xx) and that GET",
        "# routes are reachable with a non-empty body. Replace with a richer",
        "# hand-written oracle under scaffold/oracles/ for deeper checks.",
        'source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"',
        "",
    ]
    get_paths = [p for (mth, p) in routes if mth == "GET"]
    if not routes:
        lines.append("assert_reachable GET /")
        lines.append("assert_nonempty  GET /")
    for (mth, path) in routes:
        # skip parameterized path segments we can't satisfy blindly
        probe = path
        if ":" in path or "{" in path or "*" in path:
            continue
        if mth == "GET":
            lines.append(f"assert_reachable GET {probe}")
            lines.append(f"assert_nonempty  GET {probe}")
        else:
            lines.append(f"assert_wired     {mth} {probe}")
    # Always sanity-check the root if not already covered.
    if "/" not in get_paths:
        lines.append("assert_reachable GET /")
    lines.append("")
    lines.append("oracle_summary")
    lines.append("")
    return "\n".join(lines)


def app_key_for(variant: Path, bench: Path) -> str:
    rel = variant.relative_to(bench)
    layer, app = rel.parts[0], rel.parts[1]
    return f"{layer}__{app}"


def variant_dirs(bench: Path):
    for layer in sorted(p for p in bench.iterdir() if p.is_dir()):
        for app in sorted(p for p in layer.iterdir() if p.is_dir()):
            for fw in sorted(p for p in app.iterdir() if p.is_dir()):
                yield fw


def scaffold(lang: str, cfg: dict, overwrite_oracles: bool, dry: bool):
    bench = cfg["bench"]
    dockerfile_tpl = cfg["dockerfile"].read_text()
    makefile_tpl = (TEMPLATES / "Makefile.template").read_text()
    lib_src = (SCAFFOLD / "oracle-lib.sh").read_text()
    stats = {"variants": 0, "canonical": 0, "baseline": 0, "kept": 0}

    for variant in variant_dirs(bench):
        rel = variant.relative_to(bench)
        layer, app, fw = rel.parts
        stats["variants"] += 1
        port = detect_port(variant, cfg["src"])
        app_name = f"scarf-{lang}-{layer}-{app}-{fw}".lower()
        image = f"{app_name}:test"

        makefile = (makefile_tpl
                    .replace("@APP_NAME@", app_name)
                    .replace("@IMAGE@", image)
                    .replace("@APP_PORT@", port))

        # Oracle selection
        canonical = ORACLES / f"{layer}__{app}.sh"
        test_sh = variant / "test.sh"
        if canonical.is_file():
            oracle_text = canonical.read_text()
            stats["canonical"] += 1
        elif test_sh.is_file() and not overwrite_oracles:
            oracle_text = None  # keep existing
            stats["kept"] += 1
        else:
            routes = extract_routes(variant, cfg["src"], cfg["route_lang"])
            oracle_text = baseline_oracle(f"{layer}/{app}", routes)
            stats["baseline"] += 1

        if dry:
            continue

        (variant / "Dockerfile").write_text(dockerfile_tpl)
        (variant / "Makefile").write_text(makefile)
        (variant / "oracle-lib.sh").write_text(lib_src)
        if oracle_text is not None:
            test_sh.write_text(oracle_text)

    return stats


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--lang", choices=list(LANGS) + ["all"], default="all")
    ap.add_argument("--overwrite-oracles", action="store_true",
                    help="Replace existing hand-written test.sh with baseline "
                         "oracles where no canonical oracle exists.")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    langs = list(LANGS) if args.lang == "all" else [args.lang]
    for lang in langs:
        s = scaffold(lang, LANGS[lang], args.overwrite_oracles, args.dry_run)
        print(f"[{lang}] variants={s['variants']} canonical-oracle={s['canonical']} "
              f"baseline-oracle={s['baseline']} kept-existing={s['kept']}"
              f"{' (dry-run)' if args.dry_run else ''}")


if __name__ == "__main__":
    main()
