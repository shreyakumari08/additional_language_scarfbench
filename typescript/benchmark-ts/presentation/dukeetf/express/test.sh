#!/usr/bin/env bash
# Behavioral test for dukeetf — payload/shape assertions
set -euo pipefail
PORT="8080"

# tick-random
RESP=$(curl -sL "http://localhost:${PORT}/")
printf '%s' "$RESP" | grep -qE 'tick|[0-9]+\.[0-9]+|<h1>' || { echo "FAIL tick: $RESP"; exit 1; }

echo PASS
