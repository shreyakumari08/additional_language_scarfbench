#!/usr/bin/env bash
# Behavioral oracle: presentation/mood
# Renders an HTML view exposing the mood state.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET /report 200
assert_header        GET /report 'Content-Type' 'text/html'
assert_body_contains GET /report '<h1>'
assert_body_contains GET /report 'mood'
assert_body_contains GET /report 'awake'
assert_status        GET / 200

oracle_summary
