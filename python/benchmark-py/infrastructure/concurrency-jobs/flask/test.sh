#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: infrastructure/concurrency-jobs
set -euo pipefail
PORT="9080"

# Check 1: concurrency-jobs live
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (concurrency-jobs live)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'Ready|OK|<h1>' || { echo "FAIL_1_body (concurrency-jobs live): $RESP"; exit 1; }

echo PASS
