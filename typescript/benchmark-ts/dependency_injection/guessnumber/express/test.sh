#!/usr/bin/env bash
# Behavioral test for guessnumber — payload/shape assertions
set -euo pipefail
PORT="8080"

# form-html
RESP=$(curl -sL "http://localhost:${PORT}/guessnumber")
printf '%s' "$RESP" | grep -qi 'form' || { echo "FAIL form: $RESP"; exit 1; }
printf '%s' "$RESP" | grep -qi 'input' || { echo "FAIL input: $RESP"; exit 1; }
# text-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL: / missing text: $RESP"; exit 1; }

echo PASS
