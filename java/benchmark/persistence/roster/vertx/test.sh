#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080/roster}"
echo "== GET / =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root $HTTP_STATUS"; exit 1; fi
echo "== POST init =="
curl -sL -X POST "${BASE_URL}/init"
echo ""
echo "== GET leagues =="
L=$(curl -sL "${BASE_URL}/leagues")
echo "L: $L"
if ! echo "$L" | grep -q "MLS"; then echo "FAIL no MLS league"; exit 1; fi
echo "PASS"
