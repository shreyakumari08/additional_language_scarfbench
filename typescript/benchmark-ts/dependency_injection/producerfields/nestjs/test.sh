#!/usr/bin/env bash
# Behavioral test for producerfields — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/producerfields
RESP=$(curl -sL "http://localhost:${PORT}/producerfields")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: /producerfields missing marker: $RESP"; exit 1; }
# text-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL: / missing text: $RESP"; exit 1; }

echo PASS
