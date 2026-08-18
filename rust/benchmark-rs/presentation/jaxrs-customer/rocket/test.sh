#!/usr/bin/env bash
# Rust behavioral test for jaxrs-customer
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/webapi"); printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL html /webapi: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qiE 'OK|<h1>' || { echo "FAIL html /: $RESP"; exit 1; }

echo PASS
