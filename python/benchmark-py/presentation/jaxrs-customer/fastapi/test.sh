#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: presentation/jaxrs-customer
set -euo pipefail
PORT="8080"

# Check 1: customer HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/webapi")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (customer HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/webapi")
printf '%s' "$RESP" | grep -qiE 'customer|<h1>|OK' || { echo "FAIL_1_body (customer HTML): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
