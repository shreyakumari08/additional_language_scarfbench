#!/usr/bin/env bash
# Rust behavioral test for cargotracker
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/cargo-tracker/index.xhtml"); printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL html /cargo-tracker/index.xhtml: $RESP"; exit 1; }

echo PASS
