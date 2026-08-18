#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8081}"
echo "== GET / =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root $HTTP_STATUS"; exit 1; fi
echo "== POST /init =="
curl -sL -X POST "${BASE_URL}/init"
echo ""
echo "== GET /vendors =="
V=$(curl -sL "${BASE_URL}/vendors")
echo "V: $V"
if ! echo "$V" | grep -q "Acme"; then echo "FAIL no Acme vendor"; exit 1; fi
echo "== GET /orders =="
O=$(curl -sL "${BASE_URL}/orders")
echo "O: $O"
if ! echo "$O" | grep -q '"orderId":1'; then echo "FAIL no order 1"; exit 1; fi
echo "PASS"
