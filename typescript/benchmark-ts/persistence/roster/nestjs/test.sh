#!/usr/bin/env bash
# Behavioral test for roster — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/roster
RESP=$(curl -sL "http://localhost:${PORT}/roster")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: /roster missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'html' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
