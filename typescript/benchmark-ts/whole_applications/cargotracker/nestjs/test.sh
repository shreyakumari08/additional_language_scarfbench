#!/usr/bin/env bash
# Behavioral test for cargotracker — payload/shape assertions
set -euo pipefail
PORT="8080"

# html-/cargo-tracker/index.xhtml
RESP=$(curl -sL "http://localhost:${PORT}/cargo-tracker/index.xhtml")
printf '%s' "$RESP" | grep -qi '<h1>' || { echo "FAIL: /cargo-tracker/index.xhtml missing marker: $RESP"; exit 1; }

echo PASS
