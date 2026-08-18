#!/usr/bin/env bash
# Behavioral test for helloservice — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/helloservice
RESP=$(curl -sL "http://localhost:${PORT}/helloservice")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: /helloservice missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'Greetings' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
