#!/usr/bin/env bash
# Rust behavioral test for billpayment
set -euo pipefail
PORT="8080"

RESP=$(curl -sL "http://localhost:${PORT}/billpayment"); printf '%s' "$RESP" | grep -qi 'form' || { echo "FAIL form: $RESP"; exit 1; }; printf '%s' "$RESP" | grep -qi 'input' || { echo "FAIL input: $RESP"; exit 1; }
RESP=$(curl -sL "http://localhost:${PORT}/"); printf '%s' "$RESP" | grep -qE 'OK|<html' || { echo "FAIL text /: $RESP"; exit 1; }

echo PASS
