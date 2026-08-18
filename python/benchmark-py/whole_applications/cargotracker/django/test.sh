#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: whole_applications/cargotracker
set -euo pipefail
PORT="8080"

# Check 1: cargotracker HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/cargo-tracker/index.xhtml")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (cargotracker HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/cargo-tracker/index.xhtml")
printf '%s' "$RESP" | grep -qiE 'cargo|<h1>|OK' || { echo "FAIL_1_body (cargotracker HTML): $RESP"; exit 1; }

echo PASS
