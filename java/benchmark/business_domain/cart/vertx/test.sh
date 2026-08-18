#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
echo "== GET /cart =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/cart")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root $HTTP_STATUS"; exit 1; fi
echo "== POST init =="
INIT=$(curl -sL -X POST -H "Content-Type: application/json" -d '{"customerName":"Alice","customerId":"1"}' "${BASE_URL}/cart/api/initialize")
echo "INIT: $INIT"
echo "== POST add book =="
curl -sL -X POST "${BASE_URL}/cart/api/books/JavaBook" > /dev/null
echo "== GET contents =="
CONTENTS=$(curl -sL "${BASE_URL}/cart/api")
echo "CONTENTS: $CONTENTS"
if ! echo "$CONTENTS" | grep -q "JavaBook"; then echo "FAIL missing book"; exit 1; fi
echo "PASS"
