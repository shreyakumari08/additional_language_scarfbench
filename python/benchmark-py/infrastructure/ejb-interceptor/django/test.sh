#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: infrastructure/ejb-interceptor
set -euo pipefail
PORT="8080"

# Check 1: lowercase name
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/response?name=WORLD")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (lowercase name)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/response?name=WORLD")
printf '%s' "$RESP" | grep -qi 'hello' && printf '%s' "$RESP" | grep -q 'world' || { echo "FAIL_1_body (lowercase name): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
