#!/usr/bin/env bash
# Behavioral oracle: business_domain/converter
# Presents an HTML form for the currency converter interaction.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET /converter 200
assert_header        GET /converter 'Content-Type' 'text/html'
assert_body_contains GET /converter '<form'
assert_body_contains GET /converter '<input'
assert_status        GET / 200

oracle_summary
