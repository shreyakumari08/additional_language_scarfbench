#!/usr/bin/env bash
# Behavioral test for simplegreeting — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/simplegreeting
RESP=$(curl -sL "http://localhost:${PORT}/simplegreeting")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: /simplegreeting missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
