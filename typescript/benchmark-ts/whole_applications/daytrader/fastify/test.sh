#!/usr/bin/env bash
# Behavioral test for daytrader — payload/shape assertions
set -euo pipefail
PORT="9080"

# html-/daytrader/
RESP=$(curl -sL "http://localhost:${PORT}/daytrader/")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: /daytrader/ missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'html' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
