#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: whole_applications/realworld
set -euo pipefail
PORT="8080"

# Check 1: realworld tags JSON
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/api/tags")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (realworld tags JSON)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/api/tags")
printf '%s' "$RESP" | grep -q '"tags"' && printf '%s' "$RESP" | grep -q 'python' || { echo "FAIL_1_body (realworld tags JSON): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '"tags"|^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
