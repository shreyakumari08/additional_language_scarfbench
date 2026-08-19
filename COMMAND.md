# COMMAND.md — ScarfBench Runbook

Placeholders: `<APP>` `<LAYER>` `<SRC>` `<TGT>` (e.g. `roster` `persistence` `spring` `quarkus`).
Concrete example used throughout: **`roster` / `persistence` / `spring` → `quarkus`**.

---

## Page 1 — Original Harness

### Paths (this machine)

```text
scarf binary   : /Users/apple/Desktop/frame-work/scarfbench-cli/target/release/scarf   (also `scarf` on PATH)
wrapper        : /Users/apple/Desktop/frame-work/run-task.sh
agent (ccbridge): /Users/apple/Desktop/frame-work/scarfbench-evals/agents/claude-ccbridge
benchmark (java): $HOME/.scarfbench/benchmark
benchmark (oracles present): /Users/apple/Desktop/frame-work/testing/benchmark
eval-out       : /Users/apple/Desktop/frame-work/testing/eval-out
```

### 1. Run a task

Wrapper form (`<app> <source> <target> [layer]`):

```bash
cd /Users/apple/Desktop/frame-work
./run-task.sh <APP> <SRC> <TGT> <LAYER>
# example:
./run-task.sh roster spring quarkus persistence
```

Raw `scarf eval run` form (ccbridge must be up: `curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8765/` → `401`):

```bash
scarf eval run \
  --benchmark-dir "/Users/apple/Desktop/frame-work/testing/benchmark" \
  --agent-dir "/Users/apple/Desktop/frame-work/scarfbench-evals/agents/claude-ccbridge" \
  --layer <LAYER> --app <APP> \
  --source-framework <SRC> --target-framework <TGT> \
  --eval-out "/Users/apple/Desktop/frame-work/testing/eval-out" -p 1 -j 1
```

### 2. Validate / verify the output

Compute the score (build → deploy → run the held-out `smoke.py` oracle). Run **without** `--dont-rerun` the first time:

```bash
/Users/apple/Desktop/frame-work/scarfbench-cli/target/release/scarf validate \
  --conversions-dir /Users/apple/Desktop/frame-work/testing/eval-out/claude-code__<LAYER>__<APP>__<SRC>__<TGT> \
  --validations-dir  /Users/apple/Desktop/frame-work/testing/benchmark \
  --leaderboard-out
```

Re-read a cached score instantly (only after it has been validated once):

```bash
/Users/apple/Desktop/frame-work/scarfbench-cli/target/release/scarf validate \
  --conversions-dir /Users/apple/Desktop/frame-work/testing/eval-out/claude-code__<LAYER>__<APP>__<SRC>__<TGT> \
  --validations-dir  /Users/apple/Desktop/frame-work/testing/benchmark \
  --leaderboard-out \
  --dont-rerun
```

### Expected output format

`<eval-out>/claude-code__<LAYER>__<APP>__<SRC>__<TGT>/claude-code__<model>.json`:

```json
{
  "results": [{
    "from": "spring", "to": "quarkus", "layer": "persistence", "app": "roster",
    "repeats": [{ "compile": true, "run": true, "tests_passed": 14, "tests_total": 25 }]
  }]
}
```

`.../run_1/metadata.json`:

```json
{
  "status": "CONVERTED", "app": "roster", "layer": "persistence",
  "source_framework": "spring", "target_framework": "quarkus",
  "num_smoke_tests": 25, "compile_ok": "TRUE", "deploy_ok": "TRUE",
  "tests_passed": 14, "failure_reason": "11 failed, 0 errors, 14 passed out of 25 tests"
}
```

Gated **compile → deploy → test**; task success = strict `1[tests_passed == tests_total]`.

---

## Page 2 — Harbor / ScarfBench

### Paths

```text
adapter  : /Users/apple/Desktop/frame-work/harbor/adapters/scarfbench
datasets : /Users/apple/Desktop/frame-work/harbor/datasets/scarfbench
task id  : scarfbench__<LAYER>__<APP>__<SRC>__<TGT>
ccbridge secret: /Users/apple/Desktop/frame-work/ccbridge 2/.bridge_secret
```

### 1. Convert an original-harness task into Harbor format

The runtime-clone adapter pulls app source from the mirror at build time (`--task-ids <LAYER>__<APP>__<SRC>__<TGT>`):

```bash
cd /Users/apple/Desktop/frame-work/harbor/adapters/scarfbench
uv sync
uv run scarfbench \
  --task-ids <LAYER>__<APP>__<SRC>__<TGT> \
  --output-dir ../../datasets/scarfbench --overwrite
# example:
uv run scarfbench --task-ids persistence__roster__spring__quarkus \
  --output-dir ../../datasets/scarfbench --overwrite
```

Produces `datasets/scarfbench/scarfbench__<LAYER>__<APP>__<SRC>__<TGT>/`.

### 2. Harbor Oracle command

```bash
cd /Users/apple/Desktop/frame-work/harbor
uv run harbor run \
  -p datasets/scarfbench/scarfbench__<LAYER>__<APP>__<SRC>__<TGT> \
  -a oracle \
  -e docker
```

### 3. Harbor trajectory / run command (real agent, via ccbridge)

```bash
cd /Users/apple/Desktop/frame-work/harbor
BRIDGE_SECRET="$(cat '/Users/apple/Desktop/frame-work/ccbridge 2/.bridge_secret')"

uv run harbor run \
  -p datasets/scarfbench/scarfbench__<LAYER>__<APP>__<SRC>__<TGT> \
  -a claude-code \
  -m anthropic/claude-opus-4-8 \
  -e docker \
  --ae ANTHROPIC_BASE_URL=http://host.docker.internal:8765 \
  --ae ANTHROPIC_API_KEY="$BRIDGE_SECRET"
```

(For a normal key instead of ccbridge, drop the two `--ae` lines and pass `--ae ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY`.)

Export trajectories (ATIF / ShareGPT):

```bash
cd /Users/apple/Desktop/frame-work/harbor
uv run harbor traces export -p jobs --recursive --episodes all --sharegpt
```

### 4. Validation / verification

Harbor writes reward per trial; read it directly or via the viewer:

```bash
cd /Users/apple/Desktop/frame-work/harbor
J=$(ls -1dt jobs/*/ | head -1)
cat "$J"result.json
cat "$J"*/verifier/reward.txt           # 1.0 = pass
cat "$J"*/verifier/test-stdout.txt       # failure detail
uv run harbor view jobs                  # web viewer
```
