#!/usr/bin/env bash
# Behavioral oracle: dependency_injection/encoder
# The endpoint delegates to an injected encoder service that applies a +1 Caesar
# shift. We verify the injected behavior end-to-end (correct transform), over
# both GET (query) and POST (form) with the `inputString` parameter — exercising
# the DI wiring, not just reachability.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

# "hello" -> "ifmmp", "Duke" -> "Evlf" under the +1 shift.
# GET carries the value as a URL query parameter; POST as a form body.
assert_status        GET  '/encoder?inputString=hello' 200
assert_header        GET  '/encoder?inputString=hello' 'Content-Type' 'text/plain'
assert_body_contains GET  '/encoder?inputString=hello' 'ifmmp'
assert_body_contains GET  '/encoder?inputString=Duke'  'Evlf'

assert_status        POST /encoder 200 'inputString=hello'
assert_body_contains POST /encoder 'ifmmp' 'inputString=hello'

oracle_summary
