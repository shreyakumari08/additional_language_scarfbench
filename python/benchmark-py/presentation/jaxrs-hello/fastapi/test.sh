#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: presentation/jaxrs-hello
set -euo pipefail
PORT="8080"

# Check 1: helloworld greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/helloworld")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (helloworld greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/helloworld")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_1_body (helloworld greeting): $RESP"; exit 1; }

# Check 2: root greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_2_body (root greeting): $RESP"; exit 1; }

echo PASS
