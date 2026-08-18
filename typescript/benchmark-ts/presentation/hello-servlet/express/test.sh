#!/usr/bin/env bash
# Behavioral oracle: presentation/hello-servlet
# Parameterized greeting: the request parameter must flow through to the
# rendered response, and the default must be "World".
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET /greeting 200
assert_body_contains GET /greeting 'Hello'
assert_body_contains GET /greeting 'World'                 # default name
assert_body_contains GET '/greeting?name=Duke' 'Hello'
assert_body_contains GET '/greeting?name=Duke' 'Duke'      # parameter honored

oracle_summary
