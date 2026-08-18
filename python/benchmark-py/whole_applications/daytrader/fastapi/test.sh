#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: whole_applications/daytrader
set -euo pipefail
PORT="9080"

# Check 1: daytrader HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/daytrader/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (daytrader HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/daytrader/")
printf '%s' "$RESP" | grep -qiE 'daytrader|<h1>|OK' || { echo "FAIL_1_body (daytrader HTML): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
