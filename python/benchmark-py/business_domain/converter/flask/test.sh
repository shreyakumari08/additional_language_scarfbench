#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: business_domain/converter
set -euo pipefail
PORT="8080"

# Check 1: converter form
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/converter")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (converter form)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/converter")
printf '%s' "$RESP" | grep -qi 'form' && printf '%s' "$RESP" | grep -qi 'input' || { echo "FAIL_1_body (converter form): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
