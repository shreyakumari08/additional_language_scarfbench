# ScarfBench — Original Harness · Multi-Language Sources

This repository packages **only what is needed to author and run ScarfBench
migration tasks through the Original Harness** (`scarf eval run`) across four
languages: **Java, Python, TypeScript, Rust**.

It **does not** include Harbor, Harbor adapters, Harbor task-packages, Harbor
jobs, or any Harbor-side conversion tooling.

**Source paper (source of truth):**
Krishna, McGinn, Pavuluri. *ScarfBench: A Benchmark for AI-Driven Enterprise
Java Framework Migration.* (`scar.pdf` at repo root; also on arXiv:
[2605.06754](https://arxiv.org/pdf/2605.06754), project site:
[scarfbench.info](https://scarfbench.info)).

---

## What's in here

```
additional_language/
├── checklist4.md               # How to find real repos and author Original-Harness tasks (all 4 languages)
├── scar.pdf                    # Source paper
├── run-task.sh                 # Run ONE task end-to-end -> ATIF-v1.7 trajectory.json
├── generate-3-good-runs.sh     # Generate N verified Java trajectories for a fixed task
├── .gitignore
│
├── harness/                    # THE ORIGINAL HARNESS (shared across all languages)
│   ├── scarfbench-cli/         # Rust CLI providing `scarf eval run`
│   ├── scarfbench-evals/       # Agent runners (claude-ccbridge, claude-bridge, codex-*, gemini-*, ...)
│   ├── ccbridge/               # OAuth proxy on 127.0.0.1:8765 (needed by the claude-ccbridge agent)
│   ├── scripts/                # scarfbench_repo_scraper.py (repo discovery helper)
│   └── build_trajectory.py     # Consolidates run_* dirs into ATIF-v1.7 trajectory.json
│
├── java/                       # Java benchmark (paper originals + added frameworks)
│   ├── benchmark/              # spring, quarkus, jakarta (paper) + micronaut, helidon, vertx (added)
│   ├── MICRONAUT_HELIDON_VERTX_IMPLEMENTATION.md
│   └── README.md
│
├── python/
│   ├── benchmark-py/           # flask, fastapi, django
│   └── README.md
│
├── typescript/
│   ├── benchmark-ts/           # express, fastify, nestjs
│   └── README.md
│
└── rust/
    ├── benchmark-rs/           # axum, actix, rocket
    └── README.md
```

The four `benchmark*/` trees all follow the same layer taxonomy:
`business_domain/ dependency_injection/ infrastructure/ persistence/
presentation/ whole_applications/`.

---

## Prerequisites

- **Rust** (stable, 2021+) — to build the `scarf` CLI
- **Docker** — containerized compile/deploy/test gates
- **Python 3.10+** — for `build_trajectory.py` and the ccbridge OAuth proxy
- **Java 21 (Semeru or Temurin) + Maven** — for Java tasks
- **Python venv tooling** (`pip`, `uv`, or similar) — for Python tasks
- **Node.js 18+** — for TypeScript tasks
- **Cargo** — for Rust tasks (already covered)
- A working **Claude Code** login OR an `ANTHROPIC_API_KEY` if you swap agents

---

## Quickstart

### 1. Install the harness CLI

```bash
cargo install --path harness/scarfbench-cli
scarf --version    # should print
```

### 2. Start the Claude OAuth proxy (only for `claude-ccbridge` agent)

```bash
cd harness/ccbridge
python3 -m venv .venv && . .venv/bin/activate
pip install fastapi uvicorn httpx pydantic
python3 -m claude_oauth --host 127.0.0.1 --port 8765
# leave running in its own terminal
```

### 3. Run a single task

From the repo root:

```bash
./run-task.sh counter spring quarkus                    # Java
./run-task.sh counter flask fastapi                     # Python
./run-task.sh counter express fastify                   # TypeScript
./run-task.sh counter axum rocket                       # Rust
```

Output lands in `generated-trajectory/<app>__<src>__<tgt>/trajectory.json`
(ATIF-v1.7).

### 4. Authoring a new task

Follow [`checklist4.md`](./checklist4.md) — it walks through repo discovery,
framework matrix, and how to add a new `<app>/<framework>/` directory under
the appropriate benchmark tree.

---

## What is deliberately excluded

- **Harbor** (whole system): adapters, task-packages, jobs, datasets, RFCs.
- Prior conversion outputs (`conversions/`, `generated-trajectory/counter__*`).
- Build artifacts: `target/`, `node_modules/`, `.venv/`, `__pycache__/`, `dist/`, `build/`.
- Local audit / parity reports and progress docs.
- Credentials, tokens, `.env` files, and `.bridge_secret`.

---

## License

See `harness/scarfbench-cli/LICENSE` for the harness CLI license. Individual
benchmark applications are third-party open-source; their licenses live inside
each app directory.


## Harbor framework + ScarfBench adapter (added)

The **complete [Harbor](https://github.com/harbor-framework/harbor) framework**
is now vendored under [`harbor/`](./harbor/), including the ScarfBench adapter at
[`harbor/adapters/scarfbench/`](./harbor/adapters/scarfbench/). It is additive —
the original ScarfBench harness and language trees are unchanged.

- **Original ScarfBench harness** → language trees + [`harness/`](./harness/) (`scarfbench-cli`).
- **Run the same tasks through Harbor** → the full Harbor codebase in
  [`harbor/`](./harbor/) plus the ScarfBench adapter in
  [`harbor/adapters/scarfbench/`](./harbor/adapters/scarfbench/).

**No ScarfBench task data is duplicated.** The `harbor/datasets/` task
directories are intentionally omitted; the adapter reads the existing benchmark
trees in this repo (`java/benchmark/`, `python/benchmark-py/`,
`rust/benchmark-rs/`, `typescript/benchmark-ts/`) and generates Harbor tasks on
demand.

Getting started:

```bash
cd harbor
uv sync                      # install the Harbor framework
# generate + run a ScarfBench task through Harbor (see harbor/adapters/scarfbench/README.md)
uv run scarfbench --list --scarfbench-root ../java/benchmark
```

`harbor/` is the upstream Harbor framework (see its own `harbor/README.md`,
`harbor/LICENSE`); only run artifacts, virtualenvs, caches, and the ScarfBench
task data have been excluded.
