#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/contacts}"

echo "== POST contact =="
POST_RESP=$(curl -sL -X POST -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","mobilePhone":"555-123-4567"}' \
  "${BASE_URL}")
echo "POST body: $POST_RESP"

if ! echo "$POST_RESP" | grep -q '"id":'; then
  echo "FAIL - POST did not return id"
  exit 1
fi

echo "== GET list =="
LIST_RESP=$(curl -sL "${BASE_URL}")
echo "LIST: $LIST_RESP"
if ! echo "$LIST_RESP" | grep -q '"firstName":"John"'; then
  echo "FAIL - list missing created contact"
  exit 1
fi

echo "== GET /count =="
COUNT=$(curl -sL "${BASE_URL}/count")
echo "COUNT: $COUNT"
if [ "$COUNT" != "1" ]; then
  echo "FAIL - expected count 1, got $COUNT"
  exit 1
fi

echo "PASS"
exit 0
