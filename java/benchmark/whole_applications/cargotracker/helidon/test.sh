#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080/cargo-tracker}"
HTTP_STATUS=$(curl -sL -o /dev/null -w "%{http_code}" "${BASE_URL}/index.xhtml")
if [ "$HTTP_STATUS" -ne 200 ]; then echo "FAIL /index.xhtml $HTTP_STATUS"; exit 1; fi
C=$(curl -sL "${BASE_URL}/rest/cargos")
if [ -z "$C" ]; then echo "FAIL empty cargos response"; exit 1; fi
echo "PASS"
