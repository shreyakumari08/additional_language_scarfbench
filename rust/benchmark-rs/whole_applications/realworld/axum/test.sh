#!/usr/bin/env bash
# Rust behavioral test for realworld
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/api/tags"); printf '%s' "$RESP" | grep -q '"tags"' || { echo "FAIL tags: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -q '"tags"' || { echo "FAIL tags: $RESP"; exit 1; }

echo PASS
