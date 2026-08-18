# ScarfBench Task Checklist — 4 Languages — Find Real Repos & Create Migration Tasks (Original Harness only)

**Source of truth:** `scar.pdf` (ScarfBench) + the Java reference `CHECKLIST.md`. This guide
covers **only** the Original ScarfBench Harness (`scarf eval run`). It does **NOT** cover Harbor,
Harbor task-package conversion, or any Harbor/adapter validation.

**Languages in scope:** **Java · Python · TypeScript · Rust** (the four implemented in this
repository: `benchmark/`, `benchmark-py/`, `benchmark-ts/`, `benchmark-rs/`).

**What a task is (paper):** a **directed refactoring** `τ = (a, f_s, f_t)` — migrate application
`a` from a **source framework `f_s`** to a **target framework `f_t`**, **preserving externally
observable behavior, not framework syntax**. A candidate is scored by a **containerized harness**
on three gates — **compile → deploy → behavioral tests** — against a **framework-neutral oracle**
(a Gherkin scenario concretized into executable smoke tests). The task type is identical across
all four languages; only the framework ecosystem changes.

> **Tags:** **[paper]** = stated explicitly in `scar.pdf`. **[op]** = practical/operational step
> for teammates that the paper does not spell out (e.g. how to pin a Git commit). Do not treat
> [op] items as paper requirements.

> **Golden pipeline (applies to every language):**
> **Framework criteria → find a real suitable repo (auto/manual) → verify repo → select
> application/module → pin exact commit/version → create Original Harness migration task →
> run through the Original Harness → validate build / deploy / tests / reward / trajectory.**

---

## 0. Framework Matrix (source ⇄ target sets per language)

A task's `f_s` and `f_t` must both come from the **same language row**. Any framework in a row may
serve as source or target (`--source-framework` / `--target-framework` accept arbitrary strings).

| Language | Benchmark tree | Frameworks (versions as implemented) | Origin |
|---|---|---|---|
| **Java** | `benchmark/` | **Spring Boot** 3.x · **Quarkus** 3.15 · **Jakarta EE / OpenLiberty** 10 · **Micronaut** 4.7.4 · **Helidon MP** 4.1.4 · **Vert.x** 4.5.11 | Spring/Quarkus/Jakarta = ScarfBench paper originals; Micronaut/Helidon/Vert.x = repo extension frameworks |
| **Python** | `benchmark-py/` | **Flask** 3.0 · **FastAPI** 0.115 · **Django** 4.2 LTS | Repo extension (this project) |
| **TypeScript** | `benchmark-ts/` | **Express** 4.19 · **Fastify** 4.28 · **NestJS** 10.4 | Repo extension (this project) |
| **Rust** | `benchmark-rs/` | **Axum** 0.7 · **Actix Web** 4 · **Rocket** 0.5 | Repo extension (this project) |

**Shared layer taxonomy (all languages) [paper/op]:** `business_domain`, `dependency_injection`,
`infrastructure`, `persistence`, `presentation`, `whole_applications`. Every discovered repo must
map to exactly one of these layers (focused task) or to `whole_applications` (cross-layer).

---

## 1. Repository Discovery (common, language-neutral) [paper]

Find a **real, existing** open-source repository whose application uses one of the **frameworks in
its language row** (§0). **Do not create synthetic/artificial repositories.** Suitable sources are
**framework quickstarts, official reference/sample examples, and maintainer-canonical repositories**
(the paper's Java examples: `spring-projects/spring-petclinic`, Jakarta EE examples, IBM DayTrader —
find the direct equivalents for each language, see §2).

**Paper inclusion/exclusion rules (identical across all four languages) [paper]:**

- ✅ The app has **externally testable behavior** (HTTP routes, UI flows, persistent state).
- ✅ It is **framework-specific** — it actually exercises the framework (routing, DI, persistence,
  presentation, infrastructure, business services), not just language-neutral logic.
- ✅ It can be **containerized with no manual setup** beyond containerization/platforming.
- ❌ **Exclude** apps that are *primarily framework-neutral code* (plain Java/Python/TS/Rust with the
  framework barely used), *lack externally testable behavior*, or *require manual setup beyond
  containerization*.
- Choose a **focused** app (isolates one layer) or a **whole application** (cross-layer, realistic).

**Practical checks before selecting (all languages) [op]:**

- Framework **usage** confirmed and framework **version** noted (must be a version in the §0 row, or
  close enough to migrate cleanly).
- Exact **Git commit/tag** captured (reproducibility is paper-required; the SHA is the mechanism).
- Repo **builds** and the application **runs** (reaches its readiness signal).
- Required **dependencies** are available and containerizable (DB/broker → in-memory/embedded, see
  the per-language notes in §2).
- **Existing functionality and tests** located and understood.
- Whole state is **reproducible** from the pinned ref.

---

## 2. Language / Framework-Specific Discovery Signals

Use these to (a) confirm a repo really uses a candidate framework, and (b) identify `f_s` before
choosing a `f_t` from the same row. Check the **build manifest first**, then the **import/annotation
signals**, then the **entrypoint/config**.

### 2.1 Java (`benchmark/`)

**Build manifest:** `pom.xml` (Maven) or `build.gradle[.kts]` (Gradle). Look at dependency GAVs.

| Framework | Build-manifest signal | Import / annotation / config signal | Entry / run signal |
|---|---|---|---|
| **Spring Boot** | `org.springframework.boot:spring-boot-starter*` | `@SpringBootApplication`, `@RestController`, `@Service`, `@Autowired` | `SpringApplication.run`, `application.properties/yml` |
| **Quarkus** | `io.quarkus:quarkus-*` (e.g. `quarkus-resteasy-reactive`) | `@Path`, `@ApplicationScoped`, `@Inject` (Jakarta) | `application.properties`, `quarkus:dev`, `*-runner.jar` |
| **Jakarta EE / OpenLiberty** | `jakarta.platform:jakarta.jakartaee-api`, WAR packaging, Liberty plugin | `@Path`/`@GET` (JAX-RS), `@Stateless`/`@EJB`, `@Named` (CDI) | `server.xml`, `beans.xml`, `web.xml` |
| **Micronaut** | `io.micronaut:micronaut-*`, Micronaut Gradle/Maven plugin | `@Controller`, `@Singleton`, `io.micronaut.*` imports | `Application.java` with `Micronaut.run`, `application.yml` |
| **Helidon MP** | `io.helidon.microprofile*` | MicroProfile + Jakarta annotations (`@Path`, `@ApplicationScoped`) | `META-INF/microprofile-config.properties` |
| **Vert.x** | `io.vertx:vertx-*` | `AbstractVerticle`, `Router.router(vertx)`, `vertx.createHttpServer()` | `Verticle`/`Launcher` main, event-loop startup |

**Containerizable deps [op]:** prefer H2 / in-memory datasources; avoid repos wired to external
managed services with no embeddable fallback.

### 2.2 Python (`benchmark-py/`)

**Build manifest:** `requirements.txt`, `pyproject.toml`, `Pipfile`, or `setup.py`.

| Framework | Manifest signal | Import / code signal | Entry / run signal |
|---|---|---|---|
| **Flask** | `Flask` / `flask` | `from flask import Flask`, `@app.route(...)`, `Blueprint` | `app = Flask(__name__)`, `flask run`, `app.run()` |
| **FastAPI** | `fastapi`, `uvicorn` | `from fastapi import FastAPI`, `@app.get/post`, `APIRouter`, Pydantic models | `app = FastAPI()`, `uvicorn main:app` |
| **Django** | `Django` | `django.*` imports, `urls.py` `urlpatterns`, `models.Model`, `views.py` | `manage.py`, `settings.py`, `wsgi.py/asgi.py` |

**Containerizable deps [op]:** SQLite for Django/ORM apps; in-memory/dict stores for Flask/FastAPI.

### 2.3 TypeScript (`benchmark-ts/`)

**Build manifest:** `package.json` (check `dependencies`), plus `tsconfig.json`.

| Framework | `package.json` dependency | Import / code signal | Entry / run signal |
|---|---|---|---|
| **Express** | `express` | `import express from 'express'`, `app.get/post`, `express.Router()`, middleware `app.use(...)` | `const app = express(); app.listen(...)` |
| **Fastify** | `fastify` | `import Fastify from 'fastify'`, `fastify.get(...)`, plugins/`register`, JSON schemas | `const app = Fastify(); app.listen(...)` |
| **NestJS** | `@nestjs/core`, `@nestjs/common` | `@Module`, `@Controller`, `@Injectable`, decorators + DI | `NestFactory.create(AppModule)`, `main.ts` |

**Containerizable deps [op]:** prefer in-memory stores / SQLite; ensure a clean `npm ci && build`
path (no private registries).

### 2.4 Rust (`benchmark-rs/`)

**Build manifest:** `Cargo.toml` (`[dependencies]`), `Cargo.lock` for exact pins.

| Framework | `Cargo.toml` dependency | Import / code signal | Entry / run signal |
|---|---|---|---|
| **Axum** | `axum` (+ `tokio`, `tower`) | `use axum::{...}`, `Router::new().route(...)`, extractors/handlers | `#[tokio::main]`, `axum::serve(...)` |
| **Actix Web** | `actix-web` | `use actix_web::{...}`, `App::new().route/service(...)`, `#[get]/#[post]` macros | `HttpServer::new(|| App::new()...).bind(...)` |
| **Rocket** | `rocket` | `#[macro_use] extern crate rocket;`, `#[get]/#[post]`, `#[launch]`, `routes![...]` | `#[launch] fn rocket() -> _` |

**Containerizable deps [op]:** prefer in-memory state / SQLite; verify the crate versions build on
the benchmark's pinned Rust toolchain (Rust ≥ 1.88 per repo notes).

---

## 3. Repository Acceptance / Rejection Criteria

### ✅ ACCEPT when ALL hold
- Real, public, open-source repo (not synthetic, not internally fabricated).
- Uses a framework from the target language's §0 row, with a **recorded version**.
- Has **externally observable behavior** (routes / UI / persisted state) an oracle can check.
- **Framework-specific** (genuinely exercises the framework — passes the §2 signal checks).
- **Builds** and **runs** to a readiness signal from a **pinned commit/tag**.
- Dependencies are **containerizable with no manual setup** (embeddable DB/broker available).
- Maps cleanly to one of the six layers (§0) or to `whole_applications`.
- Licensed for use (permissive/open license present).

### ❌ REJECT when ANY holds
- App is **primarily framework-neutral** code (framework barely present).
- **No externally testable behavior** (pure library, CLI-only with no observable boundary).
- Requires **manual setup beyond containerization** (hand-run migrations, secret cloud creds,
  bespoke hardware, interactive install steps).
- Depends on **non-containerizable external services** with no in-memory/embedded substitute.
- Cannot be **pinned reproducibly** (no stable commit/tag; force-pushed history).
- Does **not build/run** from the pinned ref.
- Framework/version cannot be matched to a §0 row (no viable `f_s → f_t` within the language).
- **Synthetic/auto-generated** or license-incompatible.

---

## 4. Task Selection — the flow (all languages)

```
Real Repository → Exact Commit → Application/Module → Source Framework f_s → Target Framework f_t → Migration Objective
```

- **Real Repository** — a genuine public repo (§1–§3).
- **Exact Commit** — the frozen starting state (SHA/tag).
- **Application/Module** — the specific app/module inside the repo to migrate; note its **layer**.
- **Source Framework `f_s`** — what it uses today (confirmed via §2 signals).
- **Target Framework `f_t`** — another framework from the **same language row** (§0).
- **Migration Objective** — behavior to preserve, framework structure to change.

---

## 5. How a Task Is Created (step by step, language-neutral)

1. **Select the real repository/application [paper].** Pick from quickstarts / reference examples /
   maintainer-canonical repos for the chosen language; apply the include/exclude rules (§1, §3).
2. **Pin the exact commit/version [op].** Record repo URL + commit SHA/tag + module path. This
   frozen source is the **source variant `I_{a,fs}`**. (Paper requires reproducibility; pinning is how.)
3. **Understand the existing application behavior [paper].** Determine what the app does at its
   **external boundary**: routes, payloads, validation outcomes, UI flows, persisted state.
4. **Identify the framework-specific implementation [paper].** Note what is framework-bound —
   build config, dependency injection, request routing, middleware/filters, transactions/ORM,
   packaging, health/readiness checks (use the §2 per-framework markers).
5. **Select source → target framework [paper].** Fix the direction `(f_s → f_t)` within the same
   language row (§0).
6. **Define the migration objective [paper].** From the *existing* behavior (never an invented
   spec): keep observable behavior identical while adapting framework structure. Visible external
   conventions must be preserved (paper example: an `.xhtml` URL path must stay; likewise keep
   route paths, status codes, response shapes, and content types stable across the migration).
7. **Write the agent task/prompt [paper].** Instruct a migration of `I_{a,fs}` to the target
   framework. The agent uses standard **repository-editing, shell, build, and test tools**; the run
   is **one-shot, temperature 0, pass@1, no inference-time scaling**.
8. **Preserve the original application as the starting state [paper/op].** The agent starts from
   the frozen source; do not pre-modify it.
9. **Define objective validation [paper].** Author the **framework-neutral behavioral oracle `O_a`**:
   an **atomic scenario** (initial state → user/protocol operation → expected observable outcome),
   written **once as a Gherkin `.feature` file**, then **concretized as smoke tests** for the target
   variant (this repo uses a language-independent `pytest + requests` `smoke.py` against the running
   container). The oracle checks the **observable boundary only** — never internal class layout or
   source-framework unit tests.
10. **Keep the reference solution hidden [paper].** The agent receives the **source variant + the
    target-framework specification** and does **NOT** receive the expert-written target `I_{a,ft}`.
11. **Configure and run the Original Harness [paper/op].** Package the candidate into the target
    framework's **containerized runtime** (framework-specific build + Docker; focused task = single
    container, whole-app with external services = Docker Compose). Runner command in §6.
12. **Evaluate and record the result [paper].** Score the three gates and preserve the evidence
    (§9). A task is fully successful only when it **compiles, deploys, and passes the complete
    oracle**; build- or deploy-only success is **not** sufficient.

---

## 6. Original Harness Execution Flow

```
Repository + Commit → Task Instruction → Original Harness → Agent → Migration → Build → Deploy → Tests → Reward/Result → Logs/Trajectory
```

**Gate definitions [paper]:**

- **Build / Compile** — `C_τ ∈ {0,1}`; the target build succeeds.
- **Deploy** — `D_τ ∈ {0,1}`; the container/Compose stack starts within timeout and emits the
  expected readiness signal.
- **Behavioral Tests** — `O_t ∈ [0,1]`; the fraction of oracle smoke tests that pass (run only if
  deploy succeeds).

**Runner — same command for all four languages [op].** Point `--benchmark-dir` at the language's
benchmark tree and pass the framework names as free strings:

```bash
scarf eval run \
  --benchmark-dir "$HOME/.scarfbench/<benchmark|benchmark-py|benchmark-ts|benchmark-rs>" \
  --agent-dir <agent> \
  --layer <business_domain|dependency_injection|infrastructure|persistence|presentation|whole_applications> \
  --app <app> \
  --source-framework <f_s> --target-framework <f_t> \
  --eval-out <out-dir> -p 1 -j 1
```

Per-language `--benchmark-dir` / framework examples:

| Language | `--benchmark-dir` | Example `--source-framework → --target-framework` |
|---|---|---|
| Java | `.../benchmark` | `quarkus → micronaut`, `jakarta → helidon` |
| Python | `.../benchmark-py` | `flask → fastapi`, `flask → django`, `fastapi → django` |
| TypeScript | `.../benchmark-ts` | `express → fastify`, `express → nestjs`, `fastify → nestjs` |
| Rust | `.../benchmark-rs` | `axum → actix`, `axum → rocket`, `actix → rocket` |

Run settings [paper]: **temperature 0, pass@1, one-shot, no inference-time scaling**; model
`anthropic/claude-opus-4-8` via the cc bridge (`http://localhost:8765`) as used repo-wide.

---

## 7. Repository Discovery Checklist (per candidate repo)

- [ ] Language selected and its §0 framework row identified
- [ ] Repository is **real and public** (open-source, licensed, not synthetic)
- [ ] Application uses a framework from the language row (§0)
- [ ] Framework confirmed via **§2 signals** (manifest + import/annotation + entrypoint)
- [ ] Framework **version** recorded
- [ ] Exact **Git commit/tag** recorded
- [ ] Layer assigned (one of the six, or `whole_applications`)
- [ ] Repository **builds** successfully from the pinned ref
- [ ] Application **runs** and reaches readiness
- [ ] Has **externally testable behavior** (not framework-neutral-only)
- [ ] Required **dependencies** available and **containerizable** (embeddable DB/broker)
- [ ] **Existing tests / functionality** identified
- [ ] Starting state is **reproducible** from the pinned ref
- [ ] Passes ACCEPT criteria / fails no REJECT criteria (§3)

## 8. Task Creation Checklist (per task)

- [ ] Repository identified (§7 passed)
- [ ] Correct application/module + layer selected
- [ ] Correct version/commit pinned (source variant `I_{a,fs}` frozen)
- [ ] Source framework `f_s` identified
- [ ] Target framework `f_t` identified (same language row)
- [ ] Existing application behavior understood (external boundary)
- [ ] Migration objective defined from the existing application (observable conventions preserved)
- [ ] Agent task/prompt written (one-shot migration)
- [ ] Starting source state frozen (not pre-modified)
- [ ] Required dependencies/environment recorded (containerization plan)
- [ ] Behavioral oracle defined (Gherkin → smoke tests, observable boundary only)
- [ ] Reference/target solution kept hidden from the agent
- [ ] Original Harness configuration prepared (`scarf eval run` command recorded, §6)
- [ ] Run settings: temperature 0, pass@1, one-shot, no inference-time scaling

## 9. Post-Run Checklist (per run)

- [ ] **Build/compile** status recorded (`C_τ`)
- [ ] **Deployment** status recorded (`D_τ`)
- [ ] **Tests passed/failed** recorded (`O_t`, per-test counts)
- [ ] **Reward/score** recorded
- [ ] **Logs** captured
- [ ] **Agent output** (final generated implementation `Î_{a,ft}`) preserved
- [ ] **Metadata** recorded (agent/model, environment, exact harness command + config, `--benchmark-dir`)
- [ ] **Trajectory** captured **when the harness supports it** — never reconstructed after the fact
- [ ] All artifacts preserved for **reproducibility**

---

## 10. Important Rules

- ❌ Do **not** create an artificial/custom/synthetic application when an existing repository is required.
- ❌ Do **not** invent functionality that is not part of the original application.
- ❌ Do **not** weaken or remove tests to force a pass.
- ❌ Do **not** expose the reference/target solution to the agent.
- ❌ Do **not** mix `f_s`/`f_t` across different language rows (§0).
- ❌ Do **not** fabricate trajectory or results.
- ✅ Keep the **repository state and commit reproducible** (pin the SHA/tag).
- ✅ Record the **exact Original Harness command/configuration** and `--benchmark-dir`.
- ✅ This checklist is **Original Harness only** — no Harbor conversion or Harbor validation.
```
