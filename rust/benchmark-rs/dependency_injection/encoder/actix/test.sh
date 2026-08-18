#!/usr/bin/env bash
# Rust behavioral test for encoder
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/encoder?inputString=hello"); printf '%s' "$RESP" | grep -q 'Coded: ifmmp' || { echo "FAIL cipher-GET: $RESP"; exit 1; }
RESP=$(curl -sL -X POST -d "inputString=hello" "http://localhost:${PORT}/encoder"); printf '%s' "$RESP" | grep -q 'Coded: ifmmp' || { echo "FAIL cipher-POST: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
