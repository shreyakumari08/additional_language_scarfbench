#!/usr/bin/env bash
# Behavioral oracle: infrastructure/concurrency-taskcreator
# Exercises the managed-executor task-creation infrastructure; the view must
# render successfully (the concurrency work completed without error).
# NOTE: this app binds :9080 — the runner injects BASE_URL with the right port.
source "${ORACLE_LIB:-$(dirname "$0")/oracle-lib.sh}"

assert_status        GET / 200
assert_header        GET / 'Content-Type' 'text/html'
assert_body_contains GET / '<h1>'
assert_body_contains GET / 'Task Creator'

oracle_summary
