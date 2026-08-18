#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:9080/daytrader}"
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL /daytrader/ $HTTP_STATUS"; exit 1; fi
Q=$(curl -sL "${BASE_URL}/rest/quotes/s:0")
if ! echo "$Q" | grep -q "symbol"; then echo "FAIL no symbol in quote"; exit 1; fi
echo "PASS"
