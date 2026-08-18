#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: presentation/websocketbot
set -euo pipefail
PORT="8080"

# Check 1: websocketbot HTML
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (websocketbot HTML)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qiE 'websocket|bot|<h1>|OK' || { echo "FAIL_1_body (websocketbot HTML): $RESP"; exit 1; }

echo PASS
