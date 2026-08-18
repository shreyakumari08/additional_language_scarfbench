#!/usr/bin/env bash
# Behavioral test for encoder — payload/shape assertions
set -euo pipefail
PORT="8080"

# cipher-GET-/encoder
RESP=$(curl -sL "http://localhost:${PORT}/encoder?inputString=hello")
printf '%s' "$RESP" | grep -q 'Coded: ifmmp' || { echo "FAIL cipher-GET: $RESP"; exit 1; }
# cipher-POST-/encoder
RESP=$(curl -sL -X POST -d "inputString=hello" "http://localhost:${PORT}/encoder")
printf '%s' "$RESP" | grep -q 'Coded: ifmmp' || { echo "FAIL cipher-POST: $RESP"; exit 1; }
# text-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL: / missing text: $RESP"; exit 1; }

echo PASS
