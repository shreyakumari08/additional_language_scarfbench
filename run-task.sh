#!/usr/bin/env bash
# Run ONE ScarfBench migration task end-to-end and emit an ATIF-v1.7 trajectory.json.
#
#   ./run-task.sh <app> <source-framework> <target-framework> [layer]
#
# Examples:
#   ./run-task.sh counter spring quarkus
#   ./run-task.sh counter spring quarkus business_domain
#   ./run-task.sh counter flask fastapi              # Python
#   ./run-task.sh counter express fastify            # TypeScript
#   ./run-task.sh counter axum rocket                # Rust
#
# Pipeline:  scarf eval run  ->  assemble run dir (incl. agent-transcript.log)
#            ->  generated-trajectory/<app>__<src>__<tgt>/  ->  build_trajectory.py
#
# Prereqs (see README.md):
#   * scarf CLI installed (cargo install --path harness/scarfbench-cli)
#   * docker available
#   * ccbridge OAuth proxy running on :8765 (start with harness/ccbridge)
#   * Python 3 available for build_trajectory.py
set -uo pipefail

APP="${1:?usage: ./run-task.sh <app> <source> <target> [layer]}"
SRC="${2:?usage: ./run-task.sh <app> <source> <target> [layer]}"
TGT="${3:?usage: ./run-task.sh <app> <source> <target> [layer]}"
LAYER="${4:-business_domain}"

# Repo root = directory this script lives in.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

BRIDGE_URL="http://127.0.0.1:8765"

# ---- Language / benchmark selection ------------------------------------------
fw_lang() {
  case "$1" in
    spring|quarkus|jakarta|micronaut|helidon|vertx) echo java ;;
    flask|fastapi|django)                           echo python ;;
    express|fastify|nestjs)                         echo typescript ;;
    axum|rocket|actix)                              echo rust ;;
    *)                                              echo unknown ;;
  esac
}
lang_bench() {
  case "$1" in
    java)       echo "$ROOT/java/benchmark" ;;
    python)     echo "$ROOT/python/benchmark-py" ;;
    typescript) echo "$ROOT/typescript/benchmark-ts" ;;
    rust)       echo "$ROOT/rust/benchmark-rs" ;;
  esac
}

SRC_LANG="$(fw_lang "$SRC")"
TGT_LANG="$(fw_lang "$TGT")"
if [ "$SRC_LANG" = "unknown" ] || [ "$TGT_LANG" = "unknown" ]; then
  echo "[ERROR] unrecognized framework(s): src='$SRC' tgt='$TGT'" >&2
  echo "        known: java{spring,quarkus,jakarta,micronaut,helidon,vertx}" >&2
  echo "               python{flask,fastapi,django}" >&2
  echo "               typescript{express,fastify,nestjs}" >&2
  echo "               rust{axum,rocket,actix}" >&2
  exit 2
fi
if [ "$SRC_LANG" != "$TGT_LANG" ]; then
  echo "[ERROR] cross-language migration not supported: $SRC ($SRC_LANG) -> $TGT ($TGT_LANG)" >&2
  exit 2
fi
LANG_SEL="$SRC_LANG"
BENCH="$(lang_bench "$LANG_SEL")"
if [ ! -d "$BENCH" ]; then
  echo "[ERROR] benchmark dir for $LANG_SEL not found: $BENCH" >&2
  exit 1
fi
echo "[INFO] language=$LANG_SEL benchmark-dir=$BENCH"

AGENT="$ROOT/harness/scarfbench-evals/agents/claude-ccbridge"
AGENT_LOG="/tmp/claude-agent.log"

# ---- Preflight: ccbridge must already be listening --------------------------
CODE="$(curl -s -o /dev/null -w '%{http_code}' "$BRIDGE_URL/" 2>/dev/null || true)"
if [ -z "$CODE" ] || [ "$CODE" = "000" ]; then
  echo "[ERROR] ccbridge not reachable at $BRIDGE_URL — start it first:" >&2
  echo "        cd '$ROOT/harness/ccbridge' && python3 -m claude_oauth --host 127.0.0.1 --port 8765" >&2
  echo "        (needs fastapi/uvicorn/httpx/pydantic in that python)" >&2
  exit 1
fi
echo "[INFO] ccbridge up at $BRIDGE_URL (HTTP $CODE)"

EVAL_OUT="/tmp/scarf-eval-${APP}__${SRC}__${TGT}"
TASK="${APP}__${SRC}__${TGT}"
FINAL="$ROOT/generated-trajectory/$TASK"

echo "──────── running $TASK  (layer=$LAYER) ────────"
rm -rf "$EVAL_OUT" "$AGENT_LOG"

scarf eval run \
  --benchmark-dir "$BENCH" \
  --agent-dir "$AGENT" \
  --layer "$LAYER" --app "$APP" \
  --source-framework "$SRC" --target-framework "$TGT" \
  --eval-out "$EVAL_OUT" -p 1 -j 1
EXIT=$?
[ "$EXIT" -ne 0 ] && echo "[WARN] scarf eval exited $EXIT — continuing to package what exists" >&2

RUN_SRC="$(ls -d "$EVAL_OUT"/*/run_1 2>/dev/null | head -1)"
if [ -z "$RUN_SRC" ]; then
  echo "[ERROR] no run_1 produced under $EVAL_OUT" >&2
  exit 1
fi

rm -rf "$FINAL"
mkdir -p "$FINAL/run_1"
cp -R "$RUN_SRC"/. "$FINAL/run_1/"

if [ -f "$AGENT_LOG" ]; then
  cp "$AGENT_LOG" "$FINAL/run_1/agent-transcript.log"
  echo "[INFO] transcript captured -> rich ATIF"
else
  echo "[WARN] $AGENT_LOG missing -> thin ATIF (CHANGELOG only)" >&2
fi

python3 "$ROOT/harness/build_trajectory.py"

echo "✔ done: $FINAL/trajectory.json"
