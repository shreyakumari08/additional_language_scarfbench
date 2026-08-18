#!/usr/bin/env bash
# Generate N GENUINELY-migrated Claude trajectories for one task
# (default: counter, Spring -> Quarkus, Java).
#
# Each attempt runs at a FRESH unique path (no Claude-memory reuse) and is
# verified: a run is kept only if pom has quarkus deps AND no Spring code
# remains. Failures retry, up to N * 3 attempts.
#
#   ./generate-3-good-runs.sh [N]
set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

# Force native subscription auth via ccbridge.
unset ANTHROPIC_API_KEY ANTHROPIC_BASE_URL ANTHROPIC_AUTH_TOKEN

TARGET="${1:-4}"
AGENT="$ROOT/harness/scarfbench-evals/agents/claude-bridge"
BENCH="$ROOT/java/benchmark"
FINAL="$ROOT/generated-trajectory/counter__spring__quarkus_${TARGET}runs"
rm -rf "$FINAL"; mkdir -p "$FINAL"

good=0; attempt=0; MAX=$((TARGET*3))
while [ "$good" -lt "$TARGET" ] && [ "$attempt" -lt "$MAX" ]; do
  attempt=$((attempt+1))
  OUT="/tmp/scarf-gen-$attempt"
  rm -rf "$OUT"
  # wipe any Claude history for this unique path so it can't reuse a prior changelog
  rm -rf "$HOME/.claude/projects/"*scarf-gen-"$attempt"* 2>/dev/null || true

  echo "──────── attempt $attempt  (have $good/$TARGET good) ────────"
  scarf eval run --benchmark-dir "$BENCH" --agent-dir "$AGENT" \
    --layer business_domain --app counter \
    --source-framework spring --target-framework quarkus \
    --eval-out "$OUT" -p 1 -j 1

  O="$OUT/claude-code__business_domain__counter__spring__quarkus/run_1/output"

  if [ ! -d "$O" ]; then
    echo "[SKIP] attempt $attempt produced no output dir"
    continue
  fi

  # Genuineness check: must have Quarkus deps in pom and no Spring imports left.
  HAS_QUARKUS=0; HAS_SPRING=0
  if grep -Rq "quarkus" "$O"/pom.xml 2>/dev/null; then HAS_QUARKUS=1; fi
  if grep -Rq "org.springframework" "$O"/src 2>/dev/null; then HAS_SPRING=1; fi

  if [ "$HAS_QUARKUS" = "1" ] && [ "$HAS_SPRING" = "0" ]; then
    good=$((good+1))
    RUN_DIR="$FINAL/run_$good"
    mkdir -p "$RUN_DIR"
    cp -R "$O"/. "$RUN_DIR/"
    echo "[OK] kept attempt $attempt -> $RUN_DIR ($good/$TARGET good)"
  else
    echo "[REJECT] attempt $attempt: HAS_QUARKUS=$HAS_QUARKUS HAS_SPRING=$HAS_SPRING"
  fi
done

echo "──────── produced $good/$TARGET good runs in $FINAL ────────"
