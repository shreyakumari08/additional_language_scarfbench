"""
Smoke test for the "person-service" app (business_domain / piomin).

Tests the REST API at /persons/* that manages Person records (name, age,
gender, externalId). The data set is seeded at startup by Liquibase
(db/changeLog.sql): six canonical persons with ids 1..6.

Environment:
  APP_PORT   Application port (default: 8080)
  VERBOSE=1  Verbose logging

Exit codes:
  0  success (via pytest)
"""

import json
import os
import sys
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError

import pytest

APP_PORT = os.getenv("APP_PORT", "8080")
BASE = f"http://localhost:{APP_PORT}"
VERBOSE = os.getenv("VERBOSE") == "1"


def vprint(*args):
    if VERBOSE:
        print(*args)


def http(method, path, body=None, content_type="application/json", timeout=10):
    """Make an HTTP request and return (status, body_str)."""
    url = f"{BASE}{path}"
    data = None
    if body is not None:
        if content_type == "application/json":
            data = json.dumps(body).encode()
        elif content_type == "application/x-www-form-urlencoded":
            from urllib.parse import urlencode
            data = urlencode(body).encode()

    headers = {"User-Agent": "Person-Smoke/1.0"}
    if content_type and data is not None:
        headers["Content-Type"] = content_type
    if method == "GET":
        headers["Accept"] = "application/json"

    req = Request(url, data=data, method=method, headers=headers)
    try:
        with urlopen(req, timeout=timeout) as resp:
            return resp.getcode(), resp.read().decode("utf-8", "replace")
    except HTTPError as e:
        try:
            body_text = e.read().decode("utf-8", "replace")
        except Exception:
            body_text = ""
        return e.code, body_text
    except (URLError, Exception) as e:
        pytest.fail(f"Network error on {method} {path}: {e}")


def json_get(path):
    """GET JSON and parse the response."""
    status, body = http("GET", path)
    assert status == 200, f"GET {path} returned {status}: {body}"
    return json.loads(body) if body.strip() else None


# Canonical persons seeded by Liquibase (id -> name, age, gender, external_id)
SEEDED = {
    1: ("John Smith", 25, "MALE", 10),
    2: ("Paul Walker", 65, "MALE", 20),
    3: ("Lewis Hamilton", 35, "MALE", 30),
    4: ("Veronica Jones", 20, "FEMALE", 40),
    5: ("Anne Brown", 60, "FEMALE", 50),
    6: ("Felicia Scott", 45, "FEMALE", 60),
}


# ---------------------------------------------------------------------------
# Application liveness + seeded data
# ---------------------------------------------------------------------------


def test_application_is_running():
    status, _ = http("GET", "/persons")
    assert status == 200, f"GET /persons returned {status}"


def test_list_returns_json_array():
    people = json_get("/persons")
    assert isinstance(people, list), "GET /persons must return a JSON array"


def test_seeded_persons_present():
    people = json_get("/persons")
    names = {p["name"] for p in people}
    for _, (name, _a, _g, _e) in SEEDED.items():
        assert name in names, f"seeded person '{name}' missing from /persons"


def test_seeded_count_at_least_six():
    people = json_get("/persons")
    assert len(people) >= 6, f"expected >= 6 seeded persons, got {len(people)}"


# ---------------------------------------------------------------------------
# Get by id + field shape
# ---------------------------------------------------------------------------


def test_get_person_by_id():
    p = json_get("/persons/1")
    assert p is not None and p["name"] == "John Smith", f"unexpected person 1: {p}"


def test_person_has_all_fields():
    p = json_get("/persons/1")
    for field in ("id", "name", "age", "gender", "externalId"):
        assert field in p, f"person JSON missing field '{field}': {p}"


def test_person_field_values():
    p = json_get("/persons/1")
    assert p["age"] == 25, f"John Smith age should be 25, got {p['age']}"
    assert p["gender"] == "MALE", f"gender should be enum string MALE, got {p['gender']}"


def test_external_id_preserved():
    p = json_get("/persons/6")
    assert p["name"] == "Felicia Scott" and p["externalId"] == 60, \
        f"externalId not preserved for person 6: {p}"


def test_gender_is_enum_string():
    people = json_get("/persons")
    for p in people:
        assert p["gender"] in ("MALE", "FEMALE"), f"invalid gender value: {p}"


# ---------------------------------------------------------------------------
# Query by name
# ---------------------------------------------------------------------------


def test_find_by_name():
    result = json_get("/persons/name/Anne%20Brown")
    assert isinstance(result, list) and len(result) == 1, f"expected 1 match: {result}"
    assert result[0]["age"] == 60 and result[0]["gender"] == "FEMALE"


def test_find_by_name_no_match():
    result = json_get("/persons/name/Nobody%20Here")
    assert result == [], f"unknown name should yield empty list, got {result}"


# ---------------------------------------------------------------------------
# Query by age (strictly greater than)
# ---------------------------------------------------------------------------


def test_age_greater_than():
    result = json_get("/persons/age-greater-than/60")
    ages = {p["age"] for p in result}
    names = {p["name"] for p in result}
    assert "Paul Walker" in names, "age>60 should include Paul Walker (65)"
    assert all(a > 60 for a in ages), f"all results must be strictly > 60: {ages}"
    assert "John Smith" not in names, "age>60 must exclude John Smith (25)"


def test_age_greater_than_is_strict():
    result = json_get("/persons/age-greater-than/45")
    names = {p["name"] for p in result}
    assert "Felicia Scott" not in names, "age>45 must exclude Felicia Scott (45)"
    assert {"Paul Walker", "Anne Brown"}.issubset(names), \
        f"age>45 should include Paul(65) and Anne(60): {names}"


def test_age_greater_than_zero_returns_all():
    result = json_get("/persons/age-greater-than/0")
    assert len(result) >= 6, f"age>0 should return all seeded persons: {len(result)}"


# ---------------------------------------------------------------------------
# Create + read-back
# ---------------------------------------------------------------------------


def test_create_person():
    status, body = http("POST", "/persons",
                        body={"name": "Zoe Newman", "age": 28, "gender": "FEMALE", "externalId": 99})
    assert status in (200, 201), f"POST /persons returned {status}: {body}"
    created = json.loads(body)
    assert created.get("id"), f"created person must have a generated id: {created}"
    assert created["name"] == "Zoe Newman"


def test_created_person_retrievable():
    status, body = http("POST", "/persons",
                        body={"name": "Max Bauer", "age": 41, "gender": "MALE", "externalId": 77})
    assert status in (200, 201), f"POST /persons returned {status}: {body}"
    new_id = json.loads(body)["id"]
    fetched = json_get(f"/persons/{new_id}")
    assert fetched["name"] == "Max Bauer" and fetched["externalId"] == 77, \
        f"created person not retrievable by id: {fetched}"


def main():
    return pytest.main([__file__, "-v"])


if __name__ == "__main__":
    sys.exit(main())
