#!/usr/bin/env bash
# Rust behavioral test for mood
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/report"); printf '%s' "$RESP" | grep -qi 'mood' || { echo "FAIL html /report: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
