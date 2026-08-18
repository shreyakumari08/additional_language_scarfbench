#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/api/tags")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL /api/tags $HTTP_STATUS"; exit 1; fi
BODY=$(curl -sL "${BASE_URL}/api/tags")
if ! echo "$BODY" | grep -q "tags"; then echo "FAIL body missing tags"; exit 1; fi
echo "PASS"
