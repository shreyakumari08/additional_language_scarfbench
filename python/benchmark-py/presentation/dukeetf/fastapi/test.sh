#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: presentation/dukeetf
set -euo pipefail
PORT="8080"

# Check 1: dukeetf tick or HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (dukeetf tick or HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'tick|[0-9]+\.[0-9]+|<h1>' || { echo "FAIL_1_body (dukeetf tick or HTML): $RESP"; exit 1; }

echo PASS
