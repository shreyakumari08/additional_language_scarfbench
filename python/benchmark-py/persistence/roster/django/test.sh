#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: persistence/roster
set -euo pipefail
PORT="8080"

# Check 1: roster HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/roster")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (roster HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/roster")
printf '%s' "$RESP" | grep -qiE 'roster|<h1>' || { echo "FAIL_1_body (roster HTML): $RESP"; exit 1; }

# Check 2: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE '^(OK|<html)' || { echo "FAIL_2_body (root): $RESP"; exit 1; }

echo PASS
