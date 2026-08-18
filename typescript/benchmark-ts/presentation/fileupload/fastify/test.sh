#!/usr/bin/env bash
# AUTO-GENERATED baseline behavioral oracle: presentation/fileupload
# Asserts each discovered endpoint is wired (no 404/5xx) and that GET
# routes are reachable with a non-empty body. Replace with a richer
# hand-written oracle under scaffold/oracles/ for deeper checks.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_reachable GET /upload
assert_nonempty  GET /upload
assert_reachable GET /
assert_nonempty  GET /

oracle_summary
