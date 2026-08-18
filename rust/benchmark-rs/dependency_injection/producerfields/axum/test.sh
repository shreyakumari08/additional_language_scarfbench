#!/usr/bin/env bash
# Rust behavioral test for producerfields
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/producerfields"); printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL html /producerfields: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
