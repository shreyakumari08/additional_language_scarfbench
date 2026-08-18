#!/usr/bin/env bash
# Behavioral test for ejb-interceptor — payload/shape assertions
set -euo pipefail
PORT="8080"

# lowercase-named-GET
RESP=$(curl -sL "http://localhost:${PORT}/response?name=WORLD")
printf '%s' "$RESP" | grep -qi 'hello.*world' || { echo "FAIL lowercase: $RESP"; exit 1; }
# text-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL: / missing text: $RESP"; exit 1; }

echo PASS
