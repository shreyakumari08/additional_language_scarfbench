#!/usr/bin/env bash
# Rust behavioral test for order
set -euo pipefail
PORT="8081"

RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL html /: $RESP"; exit 1; }

echo PASS
