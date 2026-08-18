#!/usr/bin/env bash
# Behavioral oracle: business_domain/cart
# Session-scoped cart view: renders an HTML form; root is a liveness endpoint.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET /cart 200
assert_header        GET /cart 'Content-Type' 'text/html'
assert_body_contains GET /cart '<form'
assert_body_contains GET /cart '<input'
assert_status        GET / 200
assert_body_matches  GET / 'OK|<html'

oracle_summary
