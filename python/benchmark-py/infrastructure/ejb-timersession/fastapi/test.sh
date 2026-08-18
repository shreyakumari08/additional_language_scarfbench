#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: infrastructure/ejb-timersession
set -euo pipefail
PORT="9080"

# Check 1: timer session HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (timer session HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'Timer|timeout|OK' || { echo "FAIL_1_body (timer session HTML): $RESP"; exit 1; }

echo PASS
