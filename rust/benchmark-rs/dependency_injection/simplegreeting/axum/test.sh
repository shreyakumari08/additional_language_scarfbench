#!/usr/bin/env bash
# Rust behavioral test for simplegreeting
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/simplegreeting"); printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL html /simplegreeting: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL html /: $RESP"; exit 1; }

echo PASS
