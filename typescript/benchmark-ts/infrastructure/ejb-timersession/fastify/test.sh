#!/usr/bin/env bash
# Behavioral test for ejb-timersession — payload/shape assertions
set -euo pipefail
PORT="9080"

# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
