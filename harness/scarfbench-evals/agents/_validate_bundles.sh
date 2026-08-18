#!/usr/bin/env bash
# validate_bundles.sh
# ---------------------------------------------------------------
# Deterministic self-check for the SCARFBENCH skill-bundle matrix.
# Runs offline; no LLM calls; no network. Suitable for CI.
#
# Contract enforced:
#   1. There are 30 bundles per skill-enabled agent
#      (6 original + 24 new pairs from the 6-framework matrix).
#   2. Each bundle has exactly the 5 canonical files:
#      SKILL.md, references/{dependency,config,code}-mapping.md, references/MONOLOUGE.md
#   3. MONOLOUGE.md is byte-identical across ALL bundles within one agent
#      (paper §E.1 dedup rule).
#   4. Every bundle file is byte-identical across all 3 skill-enabled agents
#      (paper §E.1 dedup rule).
#   5. SKILL.md files start with valid frontmatter and are within
#      +/- 50% length of the baseline (spring-to-quarkus/SKILL.md).
#   6. Reference files are non-empty.
#
# Exit codes:
#   0  all checks passed
#   1+ number of failing checks
# ---------------------------------------------------------------

set -eo pipefail
cd "$(dirname "$0")"

AGENTS=(gemini-with-skills claude-with-skills codex-with-skills)

EXPECTED_PAIRS=(
  spring-to-quarkus     quarkus-to-spring
  spring-to-jakarta     jakarta-to-spring
  jakarta-to-quarkus    quarkus-to-jakarta
  spring-to-micronaut   micronaut-to-spring
  jakarta-to-micronaut  micronaut-to-jakarta
  quarkus-to-micronaut  micronaut-to-quarkus
  spring-to-helidon     helidon-to-spring
  jakarta-to-helidon    helidon-to-jakarta
  quarkus-to-helidon    helidon-to-quarkus
  micronaut-to-helidon  helidon-to-micronaut
  spring-to-vertx       vertx-to-spring
  jakarta-to-vertx      vertx-to-jakarta
  quarkus-to-vertx      vertx-to-quarkus
  micronaut-to-vertx    vertx-to-micronaut
  helidon-to-vertx      vertx-to-helidon
)

FILES=(SKILL.md references/dependency-mapping.md references/config-mapping.md references/code-mapping.md references/MONOLOUGE.md)

FAIL=0
PASS=0

fail() { echo "  FAIL: $*"; FAIL=$((FAIL + 1)); }
pass() { PASS=$((PASS + 1)); }

echo "=== Check 1: Bundle count per agent ==="
for agent in "${AGENTS[@]}"; do
  n=$(ls -d "$agent/skills/"*-to-* 2>/dev/null | wc -l | tr -d ' ')
  if [ "$n" = "30" ]; then
    pass; echo "  OK   $agent: 30 bundles"
  else
    fail "$agent has $n bundles (want 30)"
  fi
done

echo ""
echo "=== Check 2: All 30 expected pairs present per agent ==="
for agent in "${AGENTS[@]}"; do
  for pair in "${EXPECTED_PAIRS[@]}"; do
    if [ -d "$agent/skills/$pair" ]; then
      pass
    else
      fail "$agent/skills/$pair missing"
    fi
  done
done
echo "  Checked ${#AGENTS[@]} agents x ${#EXPECTED_PAIRS[@]} pairs"

echo ""
echo "=== Check 3: All 5 canonical files present per bundle ==="
missing=0
for agent in "${AGENTS[@]}"; do
  for pair in "${EXPECTED_PAIRS[@]}"; do
    for f in "${FILES[@]}"; do
      path="$agent/skills/$pair/$f"
      if [ ! -s "$path" ]; then
        fail "empty/missing: $path"
        missing=$((missing + 1))
      fi
    done
  done
done
[ "$missing" = "0" ] && { pass; echo "  OK   all 450 files present (${#AGENTS[@]} agents x 30 bundles x 5 files)"; }

echo ""
echo "=== Check 4: MONOLOUGE.md byte-identical within each agent (paper §E.1) ==="
for agent in "${AGENTS[@]}"; do
  U=$(md5 -q "$agent/skills/"*/references/MONOLOUGE.md 2>/dev/null | sort -u | wc -l | tr -d ' ')
  N=$(ls "$agent/skills/"*/references/MONOLOUGE.md 2>/dev/null | wc -l | tr -d ' ')
  if [ "$N" = "30" ] && [ "$U" = "1" ]; then
    pass; echo "  OK   $agent: 30 MONOLOUGE.md, 1 unique MD5"
  else
    fail "$agent MONOLOUGE non-identical: $N files, $U unique"
  fi
done

echo ""
echo "=== Check 5: Cross-agent byte-identity of every bundle file (paper §E.1) ==="
diverges=0
for pair in "${EXPECTED_PAIRS[@]}"; do
  for f in "${FILES[@]}"; do
    U=$(md5 -q \
      "gemini-with-skills/skills/$pair/$f" \
      "claude-with-skills/skills/$pair/$f" \
      "codex-with-skills/skills/$pair/$f" 2>/dev/null | sort -u | wc -l | tr -d ' ')
    if [ "$U" != "1" ]; then
      fail "cross-agent divergence: $pair/$f"
      diverges=$((diverges + 1))
    fi
  done
done
[ "$diverges" = "0" ] && { pass; echo "  OK   150 file-triples byte-identical across agents"; }

echo ""
echo "=== Check 6: SKILL.md frontmatter + length sanity ==="
baseline_lines=$(wc -l < "gemini-with-skills/skills/spring-to-quarkus/SKILL.md" | tr -d ' ')
lo=$((baseline_lines / 2))
hi=$((baseline_lines * 3 / 2))
bad=0
for agent in "${AGENTS[@]}"; do
  for pair in "${EXPECTED_PAIRS[@]}"; do
    path="$agent/skills/$pair/SKILL.md"
    first=$(head -1 "$path")
    n=$(wc -l < "$path" | tr -d ' ')
    if [ "$first" != "---" ]; then
      fail "$path missing frontmatter (first line: '$first')"
      bad=$((bad + 1))
    elif [ "$n" -lt "$lo" ] || [ "$n" -gt "$hi" ]; then
      fail "$path line count $n out of range [$lo,$hi]"
      bad=$((bad + 1))
    fi
  done
done
[ "$bad" = "0" ] && { pass; echo "  OK   90 SKILL.md have valid frontmatter and length within [$lo,$hi] lines"; }

echo ""
echo "=== Check 7: Vert.x target bundles carry architectural-rewrite warning ==="
warn_missing=0
for agent in "${AGENTS[@]}"; do
  for pair in spring-to-vertx jakarta-to-vertx quarkus-to-vertx micronaut-to-vertx helidon-to-vertx; do
    if ! grep -q "Architectural Rewrite Required" "$agent/skills/$pair/SKILL.md"; then
      fail "$agent/skills/$pair/SKILL.md missing Vert.x rewrite warning"
      warn_missing=$((warn_missing + 1))
    fi
  done
done
[ "$warn_missing" = "0" ] && { pass; echo "  OK   15 vertx-target SKILL.md carry the rewrite warning"; }

echo ""
echo "=== SUMMARY ==="
echo "  Passes: $PASS"
echo "  Fails:  $FAIL"

exit "$FAIL"
