#!/usr/bin/env bash
# Behavioral test - asserts payload/shape, not just HTTP 200. App: presentation/hello-servlet
set -euo pipefail
PORT="8080"

# Check 1: greeting with name
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/greeting?name=Duke")
[ "$STATUS" = "200" ] || { echo "FAIL_1_status_$STATUS (greeting with name)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/greeting?name=Duke")
printf '%s' "$RESP" | grep -qi 'hello' && printf '%s' "$RESP" | grep -qi 'Duke' || { echo "FAIL_1_body (greeting with name): $RESP"; exit 1; }

# Check 2: greeting default World
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/greeting")
[ "$STATUS" = "200" ] || { echo "FAIL_2_status_$STATUS (greeting default World)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/greeting")
printf '%s' "$RESP" | grep -qi 'hello' && printf '%s' "$RESP" | grep -qi 'World' || { echo "FAIL_2_body (greeting default World): $RESP"; exit 1; }

# Check 3: root
STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "http://localhost:${PORT}/")
[ "$STATUS" = "200" ] || { echo "FAIL_3_status_$STATUS (root)"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'hello' || { echo "FAIL_3_body (root): $RESP"; exit 1; }

echo PASS
