#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: dependency_injection/simplegreeting
set -euo pipefail
PORT="8080"

# Check 1: simplegreeting greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/simplegreeting")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (simplegreeting greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/simplegreeting")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_1_body (simplegreeting greeting): $RESP"; exit 1; }

# Check 2: root greeting
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root greeting)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'Greetings|Hello' || { echo "FAIL_2_body (root greeting): $RESP"; exit 1; }

echo PASS
