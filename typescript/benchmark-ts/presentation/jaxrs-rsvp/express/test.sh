#!/usr/bin/env bash
# Behavioral test for jaxrs-rsvp — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/webapi
RESP=$(curl -sL "http://localhost:${PORT}/webapi")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: /webapi missing marker: $RESP"; exit 1; }
# html-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qi 'html' || { echo "FAIL: / missing marker: $RESP"; exit 1; }

echo PASS
