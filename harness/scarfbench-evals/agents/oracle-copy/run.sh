#!/usr/bin/env bash
#
# Deterministic ScarfBench oracle agent.
#
# ScarfBench ships every application in every framework, so the TARGET framework
# directory IS the reference migration. This agent replaces the source workspace
# (SCARF_WORK_DIR, initially a copy of the source app) with that gold target
# implementation. It performs no model inference -- it exists to prove a task is
# solvable end-to-end (build -> deploy -> tests => reward 1.0), mirroring the
# Harbor adapter's solution/solve.sh oracle.
#
# Contract (set by `scarf eval run`, see src/eval/driver.rs):
#   SCARF_WORK_DIR       workspace to edit in place (becomes the migrated app)
#   SCARF_FRAMEWORK_TO   target framework name (e.g. "quarkus")
#   SCARF_FRAMEWORK_FROM source framework name (e.g. "spring")
#
# Extra input (exported by the caller before `scarf eval run`):
#   SCARF_ORACLE_REF     path to the ScarfBench app dir that contains the
#                        per-framework subdirectories, e.g.
#                        .../java/benchmark/persistence/roster
#
set -euo pipefail

WORK_DIR="${SCARF_WORK_DIR:-}"
FRAMEWORK_TO="${SCARF_FRAMEWORK_TO:-}"
FRAMEWORK_FROM="${SCARF_FRAMEWORK_FROM:-?}"
REF_ROOT="${SCARF_ORACLE_REF:-}"

[ -n "$WORK_DIR" ]     || { echo "[ERROR] SCARF_WORK_DIR not set" >&2; exit 1; }
[ -n "$FRAMEWORK_TO" ] || { echo "[ERROR] SCARF_FRAMEWORK_TO not set" >&2; exit 1; }
[ -n "$REF_ROOT" ]     || { echo "[ERROR] SCARF_ORACLE_REF not set (export the app dir containing <framework>/ subdirs)" >&2; exit 1; }

REF_DIR="$REF_ROOT/$FRAMEWORK_TO"
[ -d "$REF_DIR" ] || { echo "[ERROR] gold target reference not found: $REF_DIR" >&2; exit 1; }

# Idempotency: skip if we've already run (scarf may re-invoke).
if [ -f "$WORK_DIR/CHANGELOG.md" ]; then
    echo "[INFO] CHANGELOG.md already present in $WORK_DIR -- already converted, skipping." >&2
    exit 0
fi

echo "[oracle] migrating $FRAMEWORK_FROM -> $FRAMEWORK_TO"
echo "[oracle] workspace : $WORK_DIR"
echo "[oracle] reference : $REF_DIR"

# Replace the workspace contents with the gold target implementation.
rm -rf "${WORK_DIR:?}/"* 2>/dev/null || true
rm -rf "${WORK_DIR:?}/".[!.]* 2>/dev/null || true
cp -a "$REF_DIR/." "$WORK_DIR/"

# Record what happened (scarf treats CHANGELOG.md as the completion marker and
# build_trajectory.py reads it into the ATIF trajectory).
cat > "$WORK_DIR/CHANGELOG.md" <<EOF
# Migration: $FRAMEWORK_FROM -> $FRAMEWORK_TO (deterministic oracle)

Replaced the source workspace with the ScarfBench gold reference for the
\`$FRAMEWORK_TO\` framework (\`$REF_DIR\`). No model inference was performed;
this run exists to demonstrate the task is solvable end-to-end.
EOF

echo "[oracle] done -- workspace now holds the $FRAMEWORK_TO reference."
exit 0
