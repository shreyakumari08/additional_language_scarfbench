#!/usr/bin/env bash
# Rust behavioral test for ejb-interceptor
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/response?name=WORLD"); printf '%s' "$RESP" | grep -qi 'hello.*world' || { echo "FAIL lower: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
