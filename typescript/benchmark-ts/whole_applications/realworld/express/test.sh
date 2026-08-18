#!/usr/bin/env bash
# Behavioral test for realworld — payload/shape assertions
set -euo pipefail
PORT="8080"

# tags-json
RESP=$(curl -sL "http://localhost:${PORT}/api/tags")
printf '%s' "$RESP" | grep -q '"tags"' || { echo "FAIL tags: $RESP"; exit 1; }
# tags-json
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -q '"tags"' || { echo "FAIL tags: $RESP"; exit 1; }

echo PASS
