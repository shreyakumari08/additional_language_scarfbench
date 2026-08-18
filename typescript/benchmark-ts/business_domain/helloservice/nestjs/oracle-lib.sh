# shellcheck shell=bash
# =============================================================================
# ScarfBench behavioral-oracle assertion library.
#
# Sourced by every strengthened test.sh. It turns oracles from "smoke tests"
# (curl | grep one word) into behavioral checks that assert HTTP status codes,
# response structure/content, headers, and multi-step state transitions —
# following the intent of the original ScarfBench expert test suites without
# copying Java JUnit assertions verbatim.
#
# Every assertion prints a PASS/FAIL line and increments counters. The script
# exits non-zero (deterministically) if any assertion fails, via oracle_summary.
#
# Contract:
#   * BASE_URL is injected by the runner (default http://localhost:8080).
#   * curl + bash are available in the container (installed by the Dockerfiles).
# =============================================================================
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

_ORACLE_PASS=0
_ORACLE_FAIL=0

_c_ok()   { printf 'PASS  %s\n' "$1"; _ORACLE_PASS=$((_ORACLE_PASS+1)); }
_c_bad()  { printf 'FAIL  %s\n' "$1"; _ORACLE_FAIL=$((_ORACLE_FAIL+1)); }

# --- low-level HTTP -----------------------------------------------------------
# http_status METHOD PATH [DATA] -> prints numeric status code
http_status() {
  local method="$1" path="$2" data="${3:-}"
  if [ -n "$data" ]; then
    curl -sS -o /dev/null -w '%{http_code}' -X "$method" \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      --data "$data" "${BASE_URL}${path}" 2>/dev/null || echo 000
  else
    curl -sS -o /dev/null -w '%{http_code}' -X "$method" \
      "${BASE_URL}${path}" 2>/dev/null || echo 000
  fi
}

# http_body METHOD PATH [DATA] -> prints response body
http_body() {
  local method="$1" path="$2" data="${3:-}"
  if [ -n "$data" ]; then
    curl -sS -X "$method" \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      --data "$data" "${BASE_URL}${path}" 2>/dev/null
  else
    curl -sS -X "$method" "${BASE_URL}${path}" 2>/dev/null
  fi
}

# http_header METHOD PATH HEADER -> prints header value (lowercased name match)
http_header() {
  local method="$1" path="$2" hdr="$3"
  curl -sSI -X "$method" "${BASE_URL}${path}" 2>/dev/null \
    | tr -d '\r' | awk -v h="$(printf '%s' "$hdr" | tr 'A-Z' 'a-z')" \
      'BEGIN{IGNORECASE=1} tolower($1)==h":"{ $1=""; sub(/^ /,""); print }'
}

# --- assertions ---------------------------------------------------------------
# assert_status METHOD PATH EXPECTED [DATA]
assert_status() {
  local method="$1" path="$2" want="$3" data="${4:-}"
  local got; got="$(http_status "$method" "$path" "$data")"
  if [ "$got" = "$want" ]; then
    _c_ok  "$method $path -> $got"
  else
    _c_bad "$method $path -> expected $want, got $got"
  fi
}

# assert_status_in METHOD PATH DATA CODE... (accept any of the listed codes)
assert_status_in() {
  local method="$1" path="$2" data="$3"; shift 3
  local got; got="$(http_status "$method" "$path" "$data")"
  local want
  for want in "$@"; do
    if [ "$got" = "$want" ]; then _c_ok "$method $path -> $got (in $*)"; return; fi
  done
  _c_bad "$method $path -> got $got, expected one of: $*"
}

# assert_body_contains METHOD PATH NEEDLE [DATA]
assert_body_contains() {
  local method="$1" path="$2" needle="$3" data="${4:-}"
  local body; body="$(http_body "$method" "$path" "$data")"
  if printf '%s' "$body" | grep -qiF -- "$needle"; then
    _c_ok  "$method $path body contains '$needle'"
  else
    _c_bad "$method $path body missing '$needle' (got: $(printf '%s' "$body" | head -c 160))"
  fi
}

# assert_body_matches METHOD PATH REGEX [DATA]
assert_body_matches() {
  local method="$1" path="$2" re="$3" data="${4:-}"
  local body; body="$(http_body "$method" "$path" "$data")"
  if printf '%s' "$body" | grep -qiE -- "$re"; then
    _c_ok  "$method $path body matches /$re/"
  else
    _c_bad "$method $path body !~ /$re/ (got: $(printf '%s' "$body" | head -c 160))"
  fi
}

# assert_header METHOD PATH HEADER REGEX
assert_header() {
  local method="$1" path="$2" hdr="$3" re="$4"
  local val; val="$(http_header "$method" "$path" "$hdr")"
  if printf '%s' "$val" | grep -qiE -- "$re"; then
    _c_ok  "$method $path header $hdr ~ /$re/"
  else
    _c_bad "$method $path header $hdr='$val' !~ /$re/"
  fi
}

# assert_reachable METHOD PATH [DATA]  -> status must be 2xx or 3xx
assert_reachable() {
  local method="$1" path="$2" data="${3:-}"
  local got; got="$(http_status "$method" "$path" "$data")"
  if printf '%s' "$got" | grep -qE '^[23][0-9][0-9]$'; then
    _c_ok  "$method $path reachable ($got)"
  else
    _c_bad "$method $path not reachable (got $got)"
  fi
}

# assert_wired METHOD PATH [DATA] -> endpoint exists and does not crash
# (any status except 000 connection-failure, 404 not-found, or 5xx server error)
assert_wired() {
  local method="$1" path="$2" data="${3:-}"
  local got; got="$(http_status "$method" "$path" "$data")"
  if printf '%s' "$got" | grep -qE '^(000|404|5[0-9][0-9])$'; then
    _c_bad "$method $path not wired (got $got)"
  else
    _c_ok  "$method $path wired ($got)"
  fi
}

# assert_nonempty METHOD PATH [DATA] -> body must be non-empty
assert_nonempty() {
  local method="$1" path="$2" data="${3:-}"
  local body; body="$(http_body "$method" "$path" "$data")"
  if [ -n "$body" ]; then
    _c_ok  "$method $path returned non-empty body"
  else
    _c_bad "$method $path returned empty body"
  fi
}

# assert_equal LABEL ACTUAL EXPECTED
assert_equal() {
  if [ "$2" = "$3" ]; then _c_ok "$1 ($2)"; else _c_bad "$1 expected '$3' got '$2'"; fi
}

# require CONDITION_LABEL  (mark an unconditional failure, e.g. precondition)
oracle_fail() { _c_bad "$1"; }
oracle_pass() { _c_ok "$1"; }

# --- summary / exit -----------------------------------------------------------
oracle_summary() {
  printf -- '---- oracle: %d passed, %d failed ----\n' "$_ORACLE_PASS" "$_ORACLE_FAIL"
  if [ "$_ORACLE_FAIL" -ne 0 ] || [ "$_ORACLE_PASS" -eq 0 ]; then
    echo "ORACLE: FAIL"
    exit 1
  fi
  echo "ORACLE: PASS"
  exit 0
}
