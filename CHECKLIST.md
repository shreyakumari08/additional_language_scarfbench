# ScarfBench Task Authoring Checklist — Original Harness + Harbor Adapter

**Source of truth:** `scar.pdf` (Krishna, McGinn, Pavuluri et al., *ScarfBench: A Benchmark
for Cross-Framework Application Migration in Enterprise Java*, arXiv:2605.06754v2, 18 May
2026). This checklist is written **from the paper** and cross-referenced against the actual
repository state (`run-task.sh`, `harness/scarfbench-cli/`, `harbor/adapters/scarfbench/`).

**Scope:** everything needed to (a) select a real repo, (b) author a task, (c) run it through
the **Original Harness** (`scarf eval run`) **and** (d) run the same task through the **Harbor
adapter** (`uv run scarfbench` + `uv run harbor trial start`), then compare the two.

**Tags:**
- **[paper]** — stated in `scar.pdf`.
- **[repo]** — verified against the current tree at `/Users/apple/Downloads/additional_language`.
- **[op]** — operational guidance not spelled out in the paper.
- **NOT VERIFIED** — claim I could not confirm from paper + code in this checkout.

---

## 0. What is in the paper vs. what you added (READ FIRST)

`scar.pdf` covers **enterprise Java only**, three frameworks, 34 application families,
102 variants, 204 directed tasks. **Everything else in this repo is your extension** and
must be labeled as such when you report results.

| Dimension | In the paper [paper] | Added in this repo [repo] |
|---|---|---|
| Languages | Java (JDK 21) | Python, TypeScript, Rust |
| Java frameworks | Spring Boot, Jakarta EE / OpenLiberty, Quarkus | Micronaut, Helidon MP, Vert.x |
| Python frameworks | — | Flask, FastAPI, Django |
| TypeScript frameworks | — | Express, Fastify, NestJS |
| Rust frameworks | — | Axum, Actix Web, Rocket |
| Apps | 34 families (29 focused + 5 whole) | +1 whole app `piomin` (Java, Spring↔Quarkus only) |
| Metric [paper] | Strict `1[Ot=1]` behavioral equivalence per task | Same |
| Gates [paper] | Compile → Deploy → Behavioral tests (gated: `Od=0` if `Oc=0`; `Ot=0` if `Od=0`) | Same |
| Oracle [paper] | Gherkin `.feature` file → Playwright/pytest smoke tests | Same shape; Python/TS/Rust smoke suites `NOT VERIFIED` to still use Playwright everywhere |

> **Reporting rule [op]:** never blend paper-scope numbers with added-scope numbers. Report
> per-slice: `paper-Java (Spring/Jakarta/Quarkus)`, `added-Java (Micronaut/Helidon/Vertx)`,
> `added-Python`, `added-TypeScript`, `added-Rust`.

---

## 1. Framework matrix (source ⇄ target, must stay in the same language row)

Both `--source-framework` and `--target-framework` must live in the same row. `run-task.sh`
enforces this at line 63 and rejects cross-language pairs [repo].

| Language | Benchmark tree [repo] | Frameworks recognized by `run-task.sh:36-42` [repo] | Origin |
|---|---|---|---|
| Java | `java/benchmark/` | `spring`, `quarkus`, `jakarta`, `micronaut`, `helidon`, `vertx` | Spring/Quarkus/Jakarta = paper [paper]; Micronaut/Helidon/Vert.x = added [repo] |
| Python | `python/benchmark-py/` | `flask`, `fastapi`, `django` | Added [repo] |
| TypeScript | `typescript/benchmark-ts/` | `express`, `fastify`, `nestjs` | Added [repo] |
| Rust | `rust/benchmark-rs/` | `axum`, `actix`, `rocket` | Added [repo] |

Harbor adapter recognizes the same 16 framework names, plus the alias `jakartaee`, via the
`FRAMEWORK_LANGUAGES` map in `harbor/adapters/scarfbench/src/scarfbench/adapter.py:45-66`
[repo].

**Framework versions**: exact versions used inside each `<app>/<framework>/` are `NOT
VERIFIED` here at the checklist level — inspect the framework directory's build descriptor
(`pom.xml`, `requirements.txt`/`pyproject.toml`, `Cargo.toml`, `package.json`) before
authoring a task, and record what you find.

---

## 2. Application matrix (per paper, then per repo)

### 2a. Paper apps [paper] — Table 4 (focused, 29) and Table 5 (whole, 5)

| Layer | Application | Tests (J/Q/S) | KLOC | JSR anchor [paper] |
|---|---|---|---|---|
| Business Domain | `cart` | 14/8/14 | 0.82 | JSR-345 |
| Business Domain | `converter` | 21 | 0.55 | JSR-345 |
| Business Domain | `counter` | 9 | 0.55 | JSR-345 |
| Business Domain | `helloservice` | 17 | 0.56 | JSR-345 |
| Business Domain | `standalone` | 9 | 0.54 | JSR-345 |
| Dependency Inj. | `billpayment` | 14 | 0.84 | JSR-365 |
| Dependency Inj. | `decorators` | 12 | 0.76 | JSR-365 |
| Dependency Inj. | `encoder` | 11 | 0.78 | JSR-365 |
| Dependency Inj. | `guessnumber` | 9 | 0.80 | JSR-365 |
| Dependency Inj. | `producerfields` | 8 | 0.80 | JSR-365 |
| Dependency Inj. | `producermethods` | 12 | 0.77 | JSR-365 |
| Dependency Inj. | `simplegreeting` | 7 | 0.65 | JSR-365 |
| Infrastructure | `concurrency-jobs` | 8 | 0.70 | JSR-236 |
| Infrastructure | `concurrency-taskcreator` | 6 | 0.79 | JSR-236 |
| Infrastructure | `ejb-async` | 11/1/11 | 0.86 | JSR-236 |
| Infrastructure | `ejb-interceptor` | 8 | 0.56 | JSR-236 |
| Infrastructure | `ejb-timersession` | 6 | 0.55 | JSR-236 |
| Persistence | `address-book` | 10 | 1.09 | JSR-338 |
| Persistence | `order` | 11/7/11 | 1.65 | JSR-338 |
| Persistence | `roster` | 25/25/24 | 1.95 | JSR-338 |
| Presentation | `dukeetf` | 4 | 0.63 | JSR-369 |
| Presentation | `dukeetf2` | 11 | 0.71 | JSR-356 |
| Presentation | `fileupload` | 6 | 0.60 | JSR-369 |
| Presentation | `hello-servlet` | 15 | 0.47 | JSR-369 |
| Presentation | `jaxrs-customer` | 10 | 1.02 | JSR-370 |
| Presentation | `jaxrs-hello` | 8 | 0.48 | JSR-370 |
| Presentation | `jaxrs-rsvp` | 10 | 1.27 | JSR-370 |
| Presentation | `mood` | 7 | 0.64 | JSR-369 |
| Presentation | `websocketbot` | 21 | 1.02 | JSR-356 |
| **Whole (5)** | `cargotracker` | 34 | 25.72 | Jakarta EE reference |
| Whole | `coffee-shop` | 9/9/11 | 61.54 | Jakarta microservices |
| Whole | `daytrader` | 21/30/20 | 14.31 | IBM benchmark |
| Whole | `petclinic` | 36/13/13 | 17.11 | Spring reference (⚠ schema drift, see §5) |
| Whole | `realworld` | 62 | 6.40 | RealWorld REST API |

The paper attributes the focused-tier apps to `github.com/eclipse-ee4j/jakartaee-examples`
(Eclipse Foundation Jakarta EE examples) [paper §3.2]. Whole-app upstreams: `spring-petclinic`
(Spring), Jakarta cargo tracker (Eclipse), DayTrader (IBM). The remaining upstreams
(`coffee-shop`, `realworld`) are `NOT VERIFIED` from the paper text.

### 2b. Repo inventory — what actually exists on disk [repo]

Directory listing (`ls java/benchmark/<layer>/<app>/`):

- **Java** (`java/benchmark/`): all 34 paper apps present, plus **`business_domain/piomin`
  (added, Spring+Quarkus only)** — 35 apps. Every app has directories for all 6 recognized
  frameworks (`spring`, `quarkus`, `jakarta`, `micronaut`, `helidon`, `vertx`), **except**
  `piomin` which has only `spring/` and `quarkus/`.
- **Python** (`python/benchmark-py/`): 34 apps × 3 frameworks (`flask`, `fastapi`, `django`).
  Note that Jakarta-specific app names (`ejb-async`, `ejb-interceptor`, `ejb-timersession`,
  `jaxrs-*`, `hello-servlet`, `dukeetf`, `dukeetf2`) are also present here as *conceptual*
  ports — verify that each Python variant is a real behavioral port before running it, since
  the names carry Java-EE assumptions (EJB, JAX-RS, JSF) that do not map 1-to-1 to Python.
- **TypeScript** (`typescript/benchmark-ts/`): 34 apps × 3 frameworks (`express`, `fastify`,
  `nestjs`). Same naming-caveat as Python.
- **Rust** (`rust/benchmark-rs/`): 34 apps × 3 frameworks (`axum`, `actix`, `rocket`). Same
  naming-caveat.

**Stray/leaked directories to ignore [repo]** (spotted during inventory):

- `java/benchmark/dependency_injection/producerfields/file-uploads/`
- `java/benchmark/infrastructure/ejb-interceptor/file-uploads/`
- `java/benchmark/presentation/jaxrs-customer/ObjectStore/`, `.../PutObjectStoreDirHere/`
- `java/benchmark/presentation/jaxrs-rsvp/ObjectStore/`, `.../PutObjectStoreDirHere/`

These are H2/JBoss runtime artifacts leaked into the tree. Do **not** treat them as framework
variants. `run-task.sh` will refuse them (unrecognized framework name), but Harbor adapter
discovery (`test.sh` presence check, `adapter.py:191-196`) would also skip them — confirm no
stray `test.sh` was accidentally checked in inside these directories before generating tasks.

**Task-count arithmetic** (for reporting):

- Paper: 34 apps × 6 ordered pairs = **204 directed tasks** [paper Table 6].
- Repo Java tree (all 6 frameworks × 34 apps that have all 6): 34 × 30 = **1020 tasks**;
  plus `piomin` (2 frameworks: 2 pairs) = **1022 tasks**.
- Repo Python/Rust/TypeScript trees (3 frameworks × 34 apps): 34 × 6 = **204 tasks each**.
- **Total generatable in this repo: 1022 + 3 × 204 = 1634 tasks.** (Compare to Harbor adapter
  README's "~1632" — matches within rounding [repo `harbor/adapters/scarfbench/README.md`].)

---

## 3. Per-language / per-framework criteria (repo discovery + task authoring)

The criteria below apply when you want to add a **new** app (or pin an existing one to a
specific commit). Every subsection follows the same 10-point structure requested by the user.

### 3a. Java — paper frameworks (Spring Boot, Jakarta EE, Quarkus)

1. **Selecting/scraping the correct repository [paper §3.2].** Draw from *official framework
   examples* and *maintainer-canonical repos*: `github.com/eclipse-ee4j/jakartaee-examples`
   (Jakarta EE, focused tier), `github.com/spring-projects/spring-petclinic` (Spring,
   petclinic), IBM DayTrader repositories (whole tier). Exclude apps that are
   framework-neutral, lack externally testable behavior, or need manual setup beyond
   containerization.
2. **Identifying the correct application/task from the paper.** Match on the app names in
   §2a (Tables 4/5). The app name is the canonical id used by both harnesses. Focused apps
   isolate one JSR-anchored layer; whole apps combine layers.
3. **Pinning the correct commit/version [op].** Record the upstream Git SHA and tag inside
   `<app>/<framework>/PROVENANCE.md` (schema `NOT VERIFIED` — no existing PROVENANCE.md
   found in this checkout). The paper does not spell out per-app commits; use the SHA
   of the upstream file(s) you copied.
4. **Source/target framework.** Any ordered pair drawn from Java row of §1 that shares the
   same app. Paper only measures the 3×2=6 Spring/Jakarta/Quarkus pairs; extra pairs
   involving Micronaut/Helidon/Vert.x are added-scope.
5. **Build and deployment.** Java 21 (Temurin/Semeru) + Maven. Original Harness runs the
   framework directory's own `Makefile` inside Docker; Harbor adapter runs `mvn package`
   natively inside the task container (no docker-in-docker) — see `harbor/adapters/scarfbench/src/scarfbench/task-template/lang/java/tests/test.sh`
   [repo].
6. **Behavioral tests / oracle [paper §3.3].** One Gherkin `.feature` file per application
   family; concretized into per-framework Playwright + `pytest` smoke tests under
   `<app>/<framework>/smoke/` (browser scenarios) or a `requests`-only `smoke.py` (REST/SOAP
   scenarios). Reward = passed / total. Strict pass requires `Ot = 1`.
7. **Docker/environment.** Original Harness: each `<app>/<framework>/` ships its own
   `Dockerfile` + `Makefile` + `test.sh`. Harbor: agent container uses
   `eclipse-temurin:21-jdk-jammy` + Maven + Python 3.10 + Playwright/Chromium
   (`task-template/lang/java/environment/Dockerfile:16` [repo]).
8. **Original Harness task structure [repo].** Files inside `java/benchmark/<layer>/<app>/<framework>/`:
   - Required for discovery: `test.sh` (Harbor adapter's `_is_framework_dir` check,
     `adapter.py:191-196`).
   - Required for gates: `Dockerfile`, `Makefile`, `smoke/` or `smoke.py`.
   - Optional: `pom.xml` / build wrapper.
9. **Harbor task structure [repo `harbor/adapters/scarfbench/README.md`].** Generated by
   `uv run scarfbench` into `harbor/datasets/scarfbench/scarfbench__<layer>__<app>__<from>__<to>/`:
   ```
   task.toml                       # identity, metadata, verifier config
   instruction.md                  # "migrate <app> from <from> to <to>"
   environment/Dockerfile          # per-language toolchain
   environment/app/                # SOURCE app (agent workspace, harness withheld)
   tests/test.sh                   # verifier: assemble → build → deploy → pytest
   tests/verifier/                 # held-out Dockerfile + test.sh + smoke.py (target)
   solution/reference/             # gold TARGET app (oracle-only)
   solution/solve.sh               # oracle: cp reference over app
   ```
10. **Validation and reproducibility [paper Table 6 + §3.3].** An app enters the benchmark
    only after all three expert-authored variants (Spring, Jakarta, Quarkus)
    **compile + deploy + pass the complete oracle**. Per-task reproducibility relies on
    the pinned upstream SHA + the frozen framework directory + Docker-based sandboxing.

### 3b. Java — added frameworks (Micronaut, Helidon MP, Vert.x) — **added-scope [repo]**

Same 10 points as §3a with these differences:

- **Repository criteria [op].** Prefer each framework's official quickstarts:
  `micronaut-projects/micronaut-guides`, `helidon-io/helidon-examples`, `vert-x3/vertx-examples`.
  Confirm the version matches the framework directory's build descriptor.
- **Task naming clash risk.** Some paper apps depend on Java-EE-only primitives (EJB
  `@Asynchronous` in `ejb-async`; `@Interceptor` in `ejb-interceptor`; JSF in `mood`) that
  have **no direct equivalent** in Micronaut/Helidon/Vert.x. The paper explicitly notes
  `ejb-async` Quarkus is "degraded because Quarkus does not support EJB @Asynchronous;
  most scenarios are skipped" — expect the same degradation (or full skip) for the added
  frameworks. `NOT VERIFIED` per-app in this checkout.
- **Oracle preservation.** The Gherkin scenarios were authored for the paper's 3 frameworks
  and encode *observable behavior*. If the added-framework port genuinely re-implements the
  behavior, the same `smoke.py` should apply. If the port skips scenarios, adjust the test
  count and note it — do not silently pass a shortened suite. `NOT VERIFIED` whether the
  current Micronaut/Helidon/Vert.x directories ship the same `smoke.py` as their Jakarta
  siblings.
- **Reporting.** Any parity claim must exclude added frameworks from paper baselines.

### 3c. Python (Flask, FastAPI, Django) — **added-scope [repo]**

1. **Selecting/scraping [op].** Prefer official-adjacent repos:
   `pallets/flask` examples, `fastapi/full-stack-fastapi-template`,
   `django/django/tree/main/tests`, and community references
   (`testdrivenio/flask-*`, `tiangolo/full-stack-fastapi-postgresql`,
   `django-rest-framework/tutorials`).
2. **Application/task identity.** Reuse the paper's app names in §2a for cross-language
   equivalence — the *behavior* (cart, counter, converter, roster, jaxrs-hello, …) is the
   contract, not the underlying protocol. Names with Java-EE prefixes (`ejb-*`, `jaxrs-*`)
   must document the Python-idiomatic equivalent (e.g. `jaxrs-hello` → Flask blueprint or
   FastAPI router).
3. **Pinning [op].** Record upstream Git SHA in the framework directory. `NOT VERIFIED`
   whether a machine-readable manifest exists in this checkout.
4. **Source/target framework.** Any ordered pair among {flask, fastapi, django}. Cross-
   language pairs are refused (`run-task.sh:63`).
5. **Build and deployment.** Python 3.11-slim base image (Harbor:
   `task-template/lang/python/environment/Dockerfile:1` [repo]). Dependency install via
   the framework directory's `requirements.txt` or `pyproject.toml`. App boots on
   `APP_PORT=8080` inside the verifier (`task-template/lang/python/tests/test.sh:8` [repo]).
6. **Behavioral tests / oracle.** Same Gherkin-first model in principle. `smoke.py` must
   use `requests` (or Playwright for browser flows). The Harbor Python Dockerfile installs
   only `requests + pytest` — **`smoke.py` graders that need Playwright will fail with
   `ModuleNotFoundError` at verify time.** `NOT VERIFIED` which Python apps currently use
   Playwright vs. requests.
7. **Docker/environment.** Original Harness uses each `<app>/<framework>/Dockerfile`;
   Harbor uses the shared Python template Dockerfile.
8. **Original Harness task structure.** Same shape as Java — every framework dir needs
   `test.sh` (+ typically `Dockerfile` + `Makefile` + `smoke/` or `smoke.py`). `NOT VERIFIED`
   at scale — I did not open each `python/benchmark-py/*/*/test.sh`.
9. **Harbor task structure.** Same layout as Java (§3a step 9) with per-language template
   swap.
10. **Validation.** Per app: run oracle path (`solve.sh` copies `solution/reference/` over
    `environment/app/`), expect reward = 1.0. `NOT VERIFIED` for any Python task in the
    current checkout — the only oracle-verified task on disk is
    `scarfbench__business_domain__helloservice__spring__quarkus` (Java) per
    `harbor/adapters/scarfbench/README.md`.

### 3d. TypeScript (Express, Fastify, NestJS) — **added-scope [repo]**

1. **Selecting/scraping [op].** `expressjs/express` examples, `fastify/fastify/tree/main/examples`,
   `nestjs/nest/tree/master/sample`. Community references acceptable if the framework version
   is pinned.
2. **Application/task identity.** Reuse paper app names; document Node-idiomatic equivalent
   for Java-EE-flavored names.
3. **Pinning [op].** Upstream Git SHA + `package.json` lockfile (`package-lock.json`,
   `pnpm-lock.yaml`, or `yarn.lock`). `NOT VERIFIED` whether lockfiles are checked in.
4. **Source/target framework.** Any ordered pair among {express, fastify, nestjs}.
5. **Build and deployment.** Harbor image: `node:20-alpine` (`task-template/lang/typescript/environment/Dockerfile:1`
   [repo]). Deploy step: `npm start` from `<app>/<framework>/` — **assumes the framework's
   `package.json` defines a `start` script** that boots on `APP_PORT=8080`. If not, the
   verifier fails at deploy. `NOT VERIFIED` per-framework whether `start` is defined
   uniformly.
6. **Behavioral tests / oracle.** Harbor TypeScript template installs `requests + pytest`
   (Python-side grader) — **no Playwright**. UI-heavy smoke suites will fail. `NOT VERIFIED`
   which TS apps use Playwright vs. requests.
7. **Docker/environment.** As above.
8. **Original Harness task structure.** Requires `test.sh` for discovery; Dockerfile + smoke
   as per Java. `NOT VERIFIED` at scale.
9. **Harbor task structure.** Same layout, TS template.
10. **Validation.** Oracle-verify each new task individually; **no TS task is oracle-verified
    in this checkout** (see §6 caveat).

### 3e. Rust (Axum, Actix Web, Rocket) — **added-scope [repo]**

1. **Selecting/scraping [op].** `tokio-rs/axum/tree/main/examples`,
   `actix/examples`, `rwf2/Rocket/tree/master/examples`.
2. **Application/task identity.** Reuse paper app names; document Rust-idiomatic equivalent
   for Java-EE names.
3. **Pinning [op].** Upstream Git SHA + `Cargo.lock`. `NOT VERIFIED` whether `Cargo.lock`
   is checked in per app.
4. **Source/target framework.** Any ordered pair among {axum, actix, rocket}.
5. **Build and deployment.** Harbor image: `rust:1.88-slim`
   (`task-template/lang/rust/environment/Dockerfile:1` [repo]). Build: `cargo build --release`
   inside `CARGO_TARGET_DIR=/workspace/target`. Deploy: the first non-directory,
   non-`build`/`deps`/`examples`/`incremental` entry under `target/release/`
   (`task-template/lang/rust/tests/test.sh:19-24` [repo]). App boots on `APP_PORT=8080`.
6. **Behavioral tests / oracle.** Same requests-only Python grader as TS/Python. No
   Playwright. `NOT VERIFIED` per-app.
7. **Docker/environment.** As above; also installs `pkg-config` + `libssl-dev` for TLS
   crates.
8. **Original Harness task structure.** Same `test.sh`-discovery contract.
9. **Harbor task structure.** Same layout, Rust template.
10. **Validation.** Oracle-verify each new task; **no Rust task is oracle-verified in this
    checkout** (see §6 caveat).

---

## 4. Original Harness (paper's harness): what an authored task must ship

Per the paper §4 and confirmed against `run-task.sh` [repo]:

- The task **must be same-language** (`run-task.sh:63` refuses cross-language).
- The task **must** contain a `Dockerfile` in the *target* framework directory (Original
  Harness compile+deploy gates run inside that image).
- The task **must** contain a `test.sh` (or `Makefile` with a `test` target) that produces
  the reward signal. Original Harness treats each framework's `Makefile` `test` target as the
  invocation contract; Harbor's verifier replaces `make test` with a native
  `mvn package` → `java -jar` → `pytest smoke.py` path (`harbor/adapters/scarfbench/README.md`
  "Notes & Caveats" [repo]).
- The task **must** ship (or be able to recover) a `smoke.py` grader that concretizes the
  Gherkin scenarios for the target framework. Without a real `smoke.py`, the verifier
  falls back to a liveness check that is **not** the real ScarfBench grader
  (`adapter.py:380-387` [repo]).
- The task **must** honor the 3-gate pipeline: `Cτ → Dτ → Sτ`, with gating
  (`Od=0` if `Oc=0`; `Ot=0` if `Od=0`) [paper §4].
- Ports: the paper explicitly flags the **9080-vs-8080 port pitfall** for Jakarta
  EE / OpenLiberty targets (Liberty defaults to 9080; smoke tests probe 8080) [paper
  Appendix F.2 "Config / startup"]. Every new task must standardize on 8080 or override
  the smoke test port.

---

## 5. Harbor Adapter task: what generation produces

Per `harbor/adapters/scarfbench/src/scarfbench/adapter.py` [repo]:

- **Discovery** (`_load_benchmark_data`, adapter.py:198-234): walks `benchmark_root`, treats
  any directory named after a `FRAMEWORK_LANGUAGES` key containing `test.sh` as a variant,
  and emits `n × (n-1)` ordered pairs per app.
- **Harness withholding** (`_copy_app` + `HARNESS_NAMES`, adapter.py:96, 261-280): strips
  `Dockerfile`, `test.sh`, `smoke/`, `smoke.py`, `Makefile` from
  `environment/app/` (agent workspace).
- **Held-out grader** (`_copy_harness` + `_inject_smoke`, adapter.py:282-400): copies the
  *target* framework's harness into `tests/verifier/`; if `smoke.py` is absent, tries to
  recover it from `--conversions-root` (scarfbench-cli conversions tree).
- **Oracle staging** (adapter.py:467-471): copies the *target* app into
  `solution/reference/`. Harbor uploads `solution/` to `/solution` **only for the oracle
  run** — the agent's image never contains it.
- **Leak check** (`assert_no_harness_leak`, adapter.py:405-426): raises `HarnessLeakError`
  if any `HARNESS_NAMES` file appears anywhere under `environment/app/`.
- **Multi-module Maven caveat** (`harbor/adapters/scarfbench/README.md` "Notes & Caveats"
  [repo]): a few apps (e.g. `business_domain__cart` when Spring/Quarkus wraps `cart-web` +
  `cart-ejb`) have a parent `pom.xml` with `<modules>` — the runnable jar lives under a
  submodule. `tests/test.sh` should locate the jar via
  `find . -path '*/target/*.jar' -not -name '*-sources.jar' -not -name '*-tests.jar' -not -name '*-original*' | head -1`.
  Status of this fix in the current tree: **NOT VERIFIED**.

Container gate asymmetry to keep in mind before generation [repo]:

| Language | Base image | Playwright installed? | Deploy command |
|---|---|---|---|
| Java | `eclipse-temurin:21-jdk-jammy` | ✅ yes (+ Chromium) | `java -jar` |
| Python | `python:3.11-slim` | ❌ no | script-level |
| Rust | `rust:1.88-slim` | ❌ no | `cargo build --release` → first `target/release/` binary |
| TypeScript | `node:20-alpine` | ❌ no | `npm start` |

---

## 6. Current empirical status (be honest in reports) [repo]

- **Only Java Spring→Quarkus has been oracle-verified end-to-end.** Per
  `harbor/adapters/scarfbench/README.md`: *"Step 3 (oracle validation) has been reproduced
  end-to-end for `business_domain__helloservice__spring__quarkus` (reward = 1.0)"*.
- **Only 2 tasks are generated in this checkout** under `harbor/datasets/scarfbench/`:
  `scarfbench__business_domain__piomin__spring__quarkus` and
  `scarfbench__persistence__roster__spring__quarkus`.
- **`harbor/jobs/` contains 1 dated run** — `2026-08-18__13-31-35`.
- **`parity_experiment.json` is a placeholder** (all null / TODO).
- No Python/Rust/TypeScript task has been generated *and* verified against its oracle in
  this checkout. Everything else is code-supported but empirically unproven.

---

## 7. Step-by-step: creating and running ONE task end-to-end

This section walks a single task through **Original Harness → Harbor conversion → Oracle
run → Agent (trajectory) run → comparison**. Reference task used below: paper-scope
**Java `business_domain__counter__spring__quarkus`** (small, deterministic; recommend
this as the smoke test before touching any added-scope task).

### Step 0 — Preflight [repo]

```bash
# From repo root
cd /Users/apple/Downloads/additional_language

# Original Harness prerequisites
cargo install --path harness/scarfbench-cli    # scarf CLI
scarf --version                                 # verify

# Harbor prerequisites
cd harbor && uv sync && cd ..
cd harbor/adapters/scarfbench && uv sync && cd ../../..

# ccbridge (only needed for Original Harness runs; Harbor uses its own agents)
cd harness/ccbridge
python3 -m venv .venv && . .venv/bin/activate
pip install fastapi uvicorn httpx pydantic
python3 -m claude_oauth --host 127.0.0.1 --port 8765 &
cd ../..
```

⚠ **ccbridge OAuth may be expired** — `run-task.sh:79-85` will refuse to start if
`http://127.0.0.1:8765/` is unreachable. If token-refresh fails you must re-authenticate
before the Original Harness step will run.

### Step 1 — Select the task

Pick one task from §2 and confirm it lives on disk:

```bash
APP=counter LAYER=business_domain SRC=spring TGT=quarkus LANG=java
ls "java/benchmark/$LAYER/$APP/$SRC/test.sh" "java/benchmark/$LAYER/$APP/$TGT/test.sh"
```

Both `test.sh` files **must exist** — that is the discovery contract for both harnesses.

### Step 2 — Run through the Original Harness [repo]

```bash
./run-task.sh "$APP" "$SRC" "$TGT" "$LAYER"
# → produces generated-trajectory/counter__spring__quarkus/trajectory.json (ATIF-v1.7)
# → run_1/ contains agent transcript, CHANGELOG.md, and (if the harness completed)
#   the verifier's reward signal.
```

Record the outcome:

```bash
TRAJ="generated-trajectory/${APP}__${SRC}__${TGT}/trajectory.json"
jq '.final_reward, .gates' "$TRAJ" 2>/dev/null   # exact keys NOT VERIFIED; adapt to schema
```

Save the reward + first-failing-stage for the comparison table in Step 7.

### Step 3 — Select the SAME task for Harbor

Same `(layer, app, src, tgt)` tuple:

```bash
TASK_ID="${LAYER}__${APP}__${SRC}__${TGT}"
echo "$TASK_ID"    # business_domain__counter__spring__quarkus
```

### Step 4 — Convert to Harbor format [repo `harbor/adapters/scarfbench/README.md`]

Generate the Harbor task directory:

```bash
cd harbor/adapters/scarfbench
uv run scarfbench \
  --scarfbench-root ../../../java/benchmark \
  --task-ids "$TASK_ID" \
  --output-dir ../../datasets/scarfbench \
  --overwrite \
  --conversions-root <path-to-scarfbench-cli-conversions>   # optional but STRONGLY recommended
cd ../../..
```

**Why `--conversions-root` matters:** without it, `smoke.py` is not injected and the
verifier falls back to a liveness check — **not** the real ScarfBench grader (§5,
adapter.py:380-387 [repo]). For added-language tasks the recovery lookup only matches
same-language conversions; if none exist, oracle scoring will be reduced.

Confirm generation succeeded and no harness leak occurred:

```bash
TASK_DIR="harbor/datasets/scarfbench/scarfbench__${TASK_ID}"
ls "$TASK_DIR"                                     # task.toml, instruction.md, environment/, tests/, solution/
find "$TASK_DIR/environment/app" -name Dockerfile  # must be empty
find "$TASK_DIR/environment/app" -name test.sh     # must be empty
find "$TASK_DIR/environment/app" -name smoke.py    # must be empty
```

### Step 5 — Run the Harbor **oracle** command [repo]

The oracle runs `solve.sh` which copies `solution/reference/` over `environment/app/`
(adapter.py:467-471 [repo]). Expected reward: **1.0**. Anything less means the task itself
is broken, not the agent.

```bash
cd harbor
uv run harbor trial start \
  -p ../harbor/datasets/scarfbench/scarfbench__${TASK_ID} \
  -a oracle
cd ..
```

Verify:

```bash
JOB_DIR=$(ls -1dt harbor/jobs/*/ | head -1)
cat "$JOB_DIR"*/verifier/reward.txt      # expect 1.0
cat "$JOB_DIR"*/verifier/test-stdout.txt # inspect if not 1.0
```

If the oracle reward is not 1.0, **stop and fix the task before running any agent** — the
agent number would be uninterpretable. Common causes (`harbor/adapters/scarfbench/README.md`
Troubleshooting [repo]):

- `no runnable jar produced under target/` → multi-module Maven app; apply the jar-locator
  fix from §5.
- `ModuleNotFoundError: playwright` → rebuild the task image; or the framework's `smoke.py`
  requires Playwright in a non-Java template. See §5 asymmetry table.
- `deploy failed (app did not report ready in 180s)` → slow build; rerun once, then
  `docker logs` mid-run if reproducible.

### Step 6 — Run the Harbor **agent / trajectory** command [repo]

Choose an agent + model — see `harbor/AGENTS.md` for the full list. Example with Claude:

```bash
cd harbor
uv run harbor trial start \
  -p ../harbor/datasets/scarfbench/scarfbench__${TASK_ID} \
  -a claude-code \
  -m anthropic/claude-opus-4-6
cd ..
```

Or use the bundled job config:

```bash
cd harbor
uv run harbor run -c adapters/scarfbench/run_scarfbench.yaml \
  -a claude-code -m anthropic/claude-opus-4-6
cd ..
```

Collect the agent's reward and (if ATIF-compliant) trajectory:

```bash
JOB_DIR=$(ls -1dt harbor/jobs/*/ | head -1)
cat "$JOB_DIR"*/verifier/reward.txt
find "$JOB_DIR" -name 'trajectory.json' -o -name 'trace.json'   # exact path NOT VERIFIED
```

### Step 7 — Compare / validate Original vs. Harbor

Build a per-task comparison row (record in `parity_experiment.json` [repo] once you have
enough runs). Minimal comparator:

| Signal | Original Harness | Harbor | Match? |
|---|---|---|---|
| Task id | `<layer>__<app>__<src>__<tgt>` | same | must match |
| Agent + model | `claude-ccbridge` + `claude-opus-4-6` [repo `run-task.sh:75`] | whatever `-a`/`-m` you passed | may differ (record both) |
| Compile gate `Cτ` | from `trajectory.json` [schema NOT VERIFIED] | `verifier/*compile*` log | must both be 1 for a fair test comparison |
| Deploy gate `Dτ` | from `trajectory.json` | `verifier/*deploy*` log | same |
| Behavioral score `Sτ` | from `trajectory.json` | `verifier/reward.txt` | Sτ must match within the same grader; if the Harbor task fell back to the liveness grader, they will not be comparable |
| Strict pass `1[Ot=1]` | boolean | boolean | primary comparator [paper §3.1] |

Interpret the result carefully:

- **Same reward, same first-failing stage** → the two harnesses agree; task is behaviorally
  equivalent under both drivers. Adds evidence that the Harbor adapter faithfully
  approximates the Original Harness for this task.
- **Different reward with divergent `smoke.py`** → check whether Harbor fell back to the
  liveness grader (§5). Not a real disagreement; regenerate with `--conversions-root`.
- **Different reward with the same `smoke.py`** → real difference. Likely causes: (a)
  9080-vs-8080 port drift (Liberty), (b) multi-module jar-locator drift, (c)
  Docker-in-Docker vs. native-build differences (Harbor uses native `mvn package`; Original
  uses the framework's `Makefile` inside Docker). Investigate before reporting parity.
- **Original passes, Harbor errors before verifier** → likely a container-gate asymmetry
  from §5 (Playwright missing for non-Java, `npm start` missing for TypeScript, etc.).
  Not a task fault; either broaden the Harbor Dockerfile or mark the task unsupported for
  Harbor.
- **Cross-language sanity check.** If either harness returns "unknown framework" or
  refuses cross-language pairs, verify that you kept `SRC` and `TGT` in the same language
  row of §1.

### Step 8 — Distinguish paper vs added scope in the final report [op]

Every parity table you publish **must** split results by scope. Template:

```
### Parity: Original Harness vs Harbor Adapter

Paper scope (Java, Spring/Jakarta/Quarkus):
  - Focused tier (29 apps × 6 pairs = 174 tasks): Original …%, Harbor …%
  - Whole tier    ( 5 apps × 6 pairs =  30 tasks): Original …%, Harbor …%

Added scope (Java, Micronaut/Helidon/Vertx involved): …
Added scope (Python, flask/fastapi/django):          …
Added scope (TypeScript, express/fastify/nestjs):    …
Added scope (Rust, axum/actix/rocket):               …
```

Never blend the two.

---

## 8. Common failure modes to sanity-check before reporting [paper Table 3, Appendix F]

Straight from the paper's failure taxonomy — expect these when authoring or running:

**Build phase**
- `dependency_resolution`: agent-written `pom.xml`/`Cargo.toml`/`package.json`/`requirements.txt`
  requests a nonexistent artifact.
- `code_compilation`: `error: package jakarta.websocket does not exist` (missing extension),
  ambiguous imports (`java.nio.file.Path` vs `jakarta.ws.rs.Path`).
- `project_structure`: multi-module reactor references removed submodules
  (`Could not find the selected project in the reactor: roster-ear`).
- `maven_plugin`: `No plugin found for prefix 'liberty'`, unresolvable plugin versions.

**Deploy phase**
- `resource_not_found` / `manifest_error` / `artifact_not_found`: `Unable to access jarfile
  target/*.jar` when `<packaging>war</packaging>` produced no jar.
- `application_startup_failure` / `invalid_config`: the **9080-vs-8080 port pitfall**
  (Liberty defaults to 9080; smoke tests probe 8080).
- `feature_not_implemented`: `ejb-async` on Quarkus / Micronaut / Vert.x (§3b caveat).
- `connection_refused` / `deploy_timeout`: Liberty still installing feature ESAs at deploy
  boundary; increase wait or fix.
- `container_exit`: launch command mismatch (`./gradlew` on a Maven-only project).
- `unsatisfied_dependency` / `cdi_deployment_failure` / `class_not_found`: DI wiring or
  classpath break.

**Test phase**
- `http_404_not_found`: agent renamed the route, or ported REST but not the JSF `.xhtml`
  layer.
- `http_500_internal_server_error`: handler exists but throws.
- `assertion_error` / `http_400_bad_request` / `data_validation_error`: response body or
  status mismatch.
- `network_error` / `timeout_error` / `json_decode_error`: harness cannot reach or parse
  the app (port drift, HTML instead of JSON, JSF selectors pointing at old element IDs).

Use these as your triage catalog when the oracle passes but an agent run fails.

---

## 9. Explicitly UNVERIFIED items in this checklist

Every claim below is worth confirming yourself before shipping:

- Per-app `PROVENANCE.md` schema and upstream Git SHAs — **NOT VERIFIED** in this checkout.
- Whether every `python/`, `typescript/`, `rust/` app directory ships a real `smoke.py`
  (vs. a stub or a liveness-only script) — **NOT VERIFIED** at scale.
- Whether Playwright is required by any non-Java `smoke.py` in the current tree — **NOT
  VERIFIED**; if so, the Harbor Python/Rust/TS Dockerfiles need to install Playwright
  (§5 asymmetry table).
- Whether every TypeScript framework directory defines `package.json:scripts.start` on
  port 8080 — **NOT VERIFIED**.
- Whether the added Java frameworks (Micronaut/Helidon/Vert.x) ship the same
  Gherkin-derived `smoke.py` as their Jakarta/Spring/Quarkus siblings — **NOT VERIFIED**.
- Whether the `piomin` app has an oracle at all (only present under Spring + Quarkus) —
  **NOT VERIFIED**.
- Exact schema keys inside ATIF-v1.7 `trajectory.json` — **NOT VERIFIED**; consult
  `harness/build_trajectory.py` before scripting comparisons.
- Whether `--conversions-root` recovery works for non-Java tasks — the Harbor adapter's
  `_find_conversions_smoke` matches on `f"__{app}__" in name and name.endswith(f"__{target}")`
  which is language-agnostic in code, but has only been demonstrated for Java Spring→Quarkus.
- ccbridge OAuth freshness at the moment you run — see
  `memory/ccbridge-oauth-expired.md` (user memory).

---

## 10. Quick reference — one-line summaries

| I want to… | Command |
|---|---|
| List all tasks the Harbor adapter can generate | `cd harbor/adapters/scarfbench && uv run scarfbench --scarfbench-root ../../../java/benchmark --list` |
| Generate ONE task | `uv run scarfbench --scarfbench-root <lang-root> --task-ids <layer>__<app>__<src>__<tgt> --output-dir ../../datasets/scarfbench --overwrite --conversions-root <path>` |
| Run ONE task through Original Harness | `./run-task.sh <app> <src> <tgt> <layer>` |
| Run ONE Harbor task with oracle | `cd harbor && uv run harbor trial start -p ../harbor/datasets/scarfbench/scarfbench__<id> -a oracle` |
| Run ONE Harbor task with an agent | `cd harbor && uv run harbor trial start -p ../harbor/datasets/scarfbench/scarfbench__<id> -a <agent> -m <model>` |
| Read a failed verifier log | ``J=$(ls -1dt harbor/jobs/*/ \| head -1); cat "$J"*/verifier/test-stdout.txt`` |
| Check for harness leak in a generated task | `find harbor/datasets/scarfbench/scarfbench__<id>/environment/app -name Dockerfile -o -name test.sh -o -name smoke.py` |
