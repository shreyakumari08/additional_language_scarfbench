#!/usr/bin/env bash
# Behavioral test for concurrency-jobs — payload/shape assertions
set -euo pipefail
PORT="9080"

# text-/
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'Ready|<html' || { echo "FAIL: / missing text: $RESP"; exit 1; }

echo PASS
