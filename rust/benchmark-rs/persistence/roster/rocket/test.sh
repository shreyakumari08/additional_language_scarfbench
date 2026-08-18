#!/usr/bin/env bash
# Behavioral oracle: persistence/roster
# The roster view reads from the persistence layer and renders it as HTML.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET /roster 200
assert_header        GET /roster 'Content-Type' 'text/html'
assert_body_contains GET /roster '<h1>'
assert_body_contains GET /roster 'roster'
assert_status        GET / 200

oracle_summary
