#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080/helloservice}"
echo "== GET / =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root $HTTP_STATUS"; exit 1; fi
echo "== GET /sayHello =="
RESP=$(curl -sL "${BASE_URL}/sayHello?name=Alice")
echo "RESP: $RESP"
if [ "$RESP" != "Hello, Alice." ]; then echo "FAIL sayHello returned '$RESP'"; exit 1; fi
echo "PASS"
