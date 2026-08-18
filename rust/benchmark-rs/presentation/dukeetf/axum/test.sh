#!/usr/bin/env bash
# Rust behavioral test for dukeetf
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'tick|[0-9]+\.[0-9]+|<h1>' || { echo "FAIL tick: $RESP"; exit 1; }

echo PASS
