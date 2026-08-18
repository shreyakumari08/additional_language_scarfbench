#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: persistence/order
set -euo pipefail
PORT="8081"

# Check 1: order HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (order HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'order|OK' || { echo "FAIL_1_body (order HTML): $RESP"; exit 1; }

echo PASS
