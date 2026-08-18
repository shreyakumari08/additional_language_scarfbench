#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL / $HTTP_STATUS"; exit 1; fi
MENU=$(curl -sL "${BASE_URL}/menu")
if ! echo "$MENU" | grep -q "Espresso"; then echo "FAIL no Espresso"; exit 1; fi
echo "PASS"
