#!/usr/bin/env bash
# Behavioral test for jaxrs-hello — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/helloworld
RESP=$(curl -sL "http://localhost:${PORT}/helloworld")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: /helloworld missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
