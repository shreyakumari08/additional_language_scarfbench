#!/usr/bin/env bash
set -euo pipefail

# The bare resource base (/roster) has no handler; probe a real cross-framework
# endpoint. GET /roster/players lists all players (JSON 200, empty [] on the
# fresh drop-create DB) and exercises the persistence layer under test.
BASE_URL="${BASE_URL:-http://localhost:8080/roster/players}"

echo "Health check: ${BASE_URL}"
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}")

if [ "$HTTP_STATUS" -eq 200 ]; then
  echo "PASS - got HTTP ${HTTP_STATUS}"
  exit 0
else
  echo "FAIL - got HTTP ${HTTP_STATUS}"
  exit 1
fi
