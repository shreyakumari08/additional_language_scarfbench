# ScarfBench — Harbor adapter

This directory contains **only the Harbor adapter** for ScarfBench. It lets a
tasker run the **existing, original** ScarfBench tasks (already in this
repository under the language trees) through
[Harbor](https://github.com/harbor-framework/harbor) — without duplicating any
task data.

> **No task data is stored here.** The ScarfBench applications live in the
> original harness trees (`java/benchmark/`, `python/benchmark-py/`,
> `rust/benchmark-rs/`, `typescript/benchmark-ts/`). The adapter reads those
> trees and generates Harbor task directories on demand.

## What's here

```
harbor/
└── adapters/
    └── scarfbench/            # the ScarfBench → Harbor adapter (code + config)
        ├── README.md          # full adapter docs: flags, caveats, parity, troubleshooting
        ├── adapter_metadata.json
        ├── parity_experiment.json
        ├── pyproject.toml      # packaged as `harbor-scarfbench-adapter` (console script: scarfbench)
        ├── run_scarfbench.yaml # example Harbor job config (oracle agent, local Docker)
        ├── uv.lock
        └── src/scarfbench/
            ├── adapter.py      # ScarfBenchAdapter — discovery + task generation
            ├── main.py         # CLI entry point (`scarfbench`)
            └── task-template/  # template the adapter fills per task (NOT task data)
```

## Two ways to run the same tasks

| You want to run…                    | Use…                                                              |
| ----------------------------------- | ----------------------------------------------------------------- |
| the **original ScarfBench harness** | the language trees + [`harness/`](../harness/) (`scarfbench-cli`)  |
| the **same tasks through Harbor**   | this adapter (`harbor/adapters/scarfbench/`)                      |

The adapter does not change or copy the original tasks; it converts them into
Harbor task format at generation time so Harbor can execute them.

## Usage

Prerequisites: [Harbor](https://github.com/harbor-framework/harbor), `uv`,
Python 3.12+, and a running Docker daemon (Harbor builds each task image on the
host).

```bash
cd harbor/adapters/scarfbench
uv sync

# 1. Discover the tasks the adapter can generate from the original benchmark tree.
#    Point --scarfbench-root at a <layer>/<app>/<framework>/ tree — e.g. this repo's:
uv run scarfbench --list --scarfbench-root ../../../java/benchmark
#    → 204 discoverable (app, source→target) migration tasks

# 2. Generate Harbor task directories from the original tasks into a scratch dir
#    (kept OUT of this repo — task data is not vendored here).
uv run scarfbench \
  --scarfbench-root  ../../../java/benchmark \
  --output-dir       /tmp/scarfbench-harbor-tasks \
  --overwrite
#    Optionally pass --conversions-root <scarfbench-cli/conversions> to inject the
#    real held-out smoke.py grader (strongly recommended). See the adapter README.

# 3. Run a generated task through Harbor (oracle sanity → reward 1.0).
uv run harbor trial start \
  -p /tmp/scarfbench-harbor-tasks/scarfbench__business_domain__helloservice__spring__quarkus \
  -a oracle -e docker

# Or run a real agent on it:
uv run harbor trial start \
  -p /tmp/scarfbench-harbor-tasks/scarfbench__business_domain__helloservice__spring__quarkus \
  -a <agent_name> -m "<model_name>" -e docker
```

See [`adapters/scarfbench/README.md`](adapters/scarfbench/README.md) for the
complete flag reference, the harness-withholding / no-answer-leak guarantees,
the multi-module Maven policy, parity notes, and troubleshooting.

## Verified

- `import scarfbench` / `scarfbench.adapter` / `scarfbench.main` — OK.
- `scarfbench --list --scarfbench-root java/benchmark` discovers **204** tasks
  against this repository's original ScarfBench tree (matching the documented
  204-task superset), confirming the adapter connects the original task
  structure to Harbor without duplicating any task data.
</content>
