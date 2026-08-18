#!/usr/bin/env bash
# Verify ONE ScarfBench variant with the containerized behavioral oracle.
#
#   ./verify-task.sh <framework> <app> [layer]
#
# Examples:
#   ./verify-task.sh express  counter                       # TypeScript
#   ./verify-task.sh axum     encoder dependency_injection  # Rust
#   ./verify-task.sh fastify  cart    business_domain
#
# Runs `make test` in the variant directory, which builds the image, deploys the
# container, runs the strengthened oracle (test.sh) against the DEPLOYED server,
# tears the container down, and exits with a deterministic pass/fail code.
#
# This is the oracle counterpart to run-task.sh (which drives an agent). It does
# NOT assume the app is already running — the container starts it.
set -uo pipefail

FW="${1:?usage: ./verify-task.sh <framework> <app> [layer]}"
APP="${2:?usage: ./verify-task.sh <framework> <app> [layer]}"
LAYER="${3:-}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

fw_lang() {
  case "$1" in
    spring|quarkus|jakarta|micronaut|helidon|vertx) echo java ;;
    flask|fastapi|django)                           echo python ;;
    express|fastify|nestjs)                          echo typescript ;;
    axum|rocket|actix)                               echo rust ;;
    *)                                               echo unknown ;;
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

LANG_SEL="$(fw_lang "$FW")"
if [ "$LANG_SEL" = "unknown" ]; then
  echo "[ERROR] unknown framework: $FW" >&2; exit 2
fi
BENCH="$(lang_bench "$LANG_SEL")"

# Resolve the variant directory (layer optional — search if not given).
if [ -n "$LAYER" ]; then
  VARIANT="$BENCH/$LAYER/$APP/$FW"
else
  VARIANT="$(find "$BENCH" -mindepth 3 -maxdepth 3 -type d \
              -path "*/$APP/$FW" 2>/dev/null | head -1)"
fi

if [ -z "${VARIANT:-}" ] || [ ! -d "$VARIANT" ]; then
  echo "[ERROR] variant not found: framework=$FW app=$APP layer=${LAYER:-<any>}" >&2
  exit 1
fi
if [ ! -f "$VARIANT/Makefile" ]; then
  echo "[ERROR] no Makefile in $VARIANT — run harness/scaffold/scaffold.py first" >&2
  exit 1
fi

echo "[INFO] verifying $VARIANT"
cd "$VARIANT"
make test
