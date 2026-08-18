#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: business_domain/helloservice
set -euo pipefail
PORT="8080"

# Check 1: helloservice greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/helloservice")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (helloservice greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/helloservice")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_1_body (helloservice greeting): $RESP"; exit 1; }

# Check 2: root greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_2_body (root greeting): $RESP"; exit 1; }

echo PASS
