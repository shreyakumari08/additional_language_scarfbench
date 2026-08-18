#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:9080}"
echo "== GET / =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root $HTTP_STATUS"; exit 1; fi
echo "== POST /send =="
curl -sL -X POST -d "to=test@a.com&subject=hi&body=body" "${BASE_URL}/send" > /dev/null
sleep 1
echo "== GET /sent =="
SENT=$(curl -sL "${BASE_URL}/sent")
echo "SENT: $SENT"
if [ "$SENT" -lt "1" ]; then echo "FAIL sent<1"; exit 1; fi
echo "PASS"
