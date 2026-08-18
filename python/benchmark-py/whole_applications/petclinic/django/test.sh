#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: whole_applications/petclinic
set -euo pipefail
PORT="8080"

# Check 1: petclinic HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (petclinic HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'petclinic|pet|<h1>|OK' || { echo "FAIL_1_body (petclinic HTML): $RESP"; exit 1; }

echo PASS
