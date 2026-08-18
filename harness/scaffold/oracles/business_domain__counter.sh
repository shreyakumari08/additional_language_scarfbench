#!/usr/bin/env bash
# Behavioral oracle: business_domain/counter
# Verifies stateful behavior — the counter must report a plain-text access count
# that strictly increases on each request (application-scoped state), which is
# the observable contract the migration must preserve.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status GET / 200
assert_status GET /counter 200
assert_header GET /counter 'Content-Type' 'text/plain'
assert_body_matches GET /counter 'accessed[[:space:]]+[0-9]+[[:space:]]+time'

# --- stateful check: successive reads must strictly increase the count --------
n1=$(http_body GET /counter | grep -oE '[0-9]+' | head -1)
n2=$(http_body GET /counter | grep -oE '[0-9]+' | head -1)
if [ -n "$n1" ] && [ -n "$n2" ] && [ "$n2" -gt "$n1" ]; then
  oracle_pass "counter increments across requests ($n1 -> $n2)"
else
  oracle_fail "counter did not increment (got '$n1' then '$n2')"
fi

oracle_summary
