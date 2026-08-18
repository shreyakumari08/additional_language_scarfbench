#!/usr/bin/env bash
# Rust behavioral test for concurrency-jobs
set -euo pipefail
PORT="9080"

RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'Ready|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
