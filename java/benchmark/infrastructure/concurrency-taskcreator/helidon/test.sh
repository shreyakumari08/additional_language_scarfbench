#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:9080}"
echo "== GET / =="
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL root got $HTTP_STATUS"; exit 1; fi
echo "== GET /tasks =="
TASKS=$(curl -sL "${BASE_URL}/tasks")
echo "TASKS: $TASKS"
if ! echo "$TASKS" | grep -q '"name":"immediate"'; then echo "FAIL immediate task not seen"; exit 1; fi
echo "PASS"
