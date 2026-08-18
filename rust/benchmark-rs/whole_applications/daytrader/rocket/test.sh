#!/usr/bin/env bash
# Rust behavioral test for daytrader
set -euo pipefail
PORT="9080"

RESP=$(curl -sL "http://localhost:${PORT}/daytrader/"); printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL html /daytrader/: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qiE 'OK|<h1>' || { echo "FAIL html /: $RESP"; exit 1; }

echo PASS
