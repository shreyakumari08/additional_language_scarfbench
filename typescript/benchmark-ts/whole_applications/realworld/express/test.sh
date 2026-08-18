#!/usr/bin/env bash
# Behavioral oracle: whole_applications/realworld
# Whole-app tier: verify the JSON API contract of the tags endpoint. Tag values
# are language-specific, so we assert the JSON *structure* (a non-empty `tags`
# array) rather than exact contents — a behavior-preserving invariant.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status GET /api/tags 200
assert_header GET /api/tags 'Content-Type' 'application/json'
assert_body_matches GET /api/tags '"tags"[[:space:]]*:[[:space:]]*\['
# non-empty array: at least one quoted string element inside tags
assert_body_matches GET /api/tags '"tags"[[:space:]]*:[[:space:]]*\[[[:space:]]*"[^"]+"'

oracle_summary
