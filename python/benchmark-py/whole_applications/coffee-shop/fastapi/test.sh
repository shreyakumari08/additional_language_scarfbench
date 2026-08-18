#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: whole_applications/coffee-shop
set -euo pipefail
PORT="8080"

# Check 1: coffee-shop HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (coffee-shop HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'coffee|shop|<h1>|OK' || { echo "FAIL_1_body (coffee-shop HTML): $RESP"; exit 1; }

echo PASS
