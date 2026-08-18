#!/usr/bin/env bash
# Rust behavioral test for jaxrs-hello
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/helloworld"); printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL html /helloworld: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL html /: $RESP"; exit 1; }

echo PASS
