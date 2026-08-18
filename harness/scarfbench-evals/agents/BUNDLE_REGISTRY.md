# SCARFBENCH Skill-Bundle Registry

**Generated:** 2026-08-14
**Scope:** all skill bundles for the 6-framework matrix (Spring, Jakarta EE, Quarkus, Micronaut, Helidon MP, Vert.x).
**Agents covered:** `gemini-with-skills`, `claude-with-skills`, `codex-with-skills` (paper §E.1: bundles must be byte-identical across skill-enabled agents).

---

## Summary

| Metric | Value |
|---|---|
| Frameworks in matrix | 6 |
| Directed pairs (n × (n−1)) | 30 |
| Bundles per skill-enabled agent | 30 |
| Skill-enabled agents | 3 |
| Bundle instances (30 × 3) | 90 |
| Files per bundle | 5 (SKILL.md + 4 references) |
| Total files on disk | 450 |
| Validation checks passing | 100 / 100 (see `_validate_bundles.sh`) |

Run `./_validate_bundles.sh` to reproduce the check locally.

---

## Provenance labels

Each bundle carries one of the following provenance labels. **This is the skeptic-required column** — it tells you how much to trust the mapping content.

| Label | Meaning |
|---|---|
| **GROUND-TRUTH** | The bundle's mapping tables were derived from paired implementations that exist in this repo (`benchmark/**/spring`, `.../jakarta`, `.../quarkus`). Verified against actual `pom.xml` and source diffs. |
| **DOCS-CITED** | The bundle's mapping tables cite official framework documentation URLs. Content is technically correct per docs but has NOT been validated against a paired implementation in this repo (because Micronaut/Helidon/Vert.x variants do not yet exist). |
| **NEEDS-IMPL** | Same as DOCS-CITED, additionally flagged because the target framework is architecturally distant from the source (e.g., anything ↔ Vert.x). The bundle warns the agent, but agents may still fail; scores will be low until real paired implementations are produced. |

---

## Registry

### Existing pairs (6) — carried forward untouched

| # | Bundle | Provenance | Notes |
|---|---|---|---|
| 1 | `spring-to-quarkus` | GROUND-TRUTH | Paired implementations exist for all 34 apps; paper §5 measured. |
| 2 | `quarkus-to-spring` | GROUND-TRUTH | Same. |
| 3 | `spring-to-jakarta` | GROUND-TRUTH | Same. |
| 4 | `jakarta-to-spring` | GROUND-TRUTH | Same. |
| 5 | `quarkus-to-jakarta` | GROUND-TRUTH | Same. |
| 6 | `jakarta-to-quarkus` | GROUND-TRUTH | Same. |

### New pairs — Micronaut (6)

| # | Bundle | Provenance | Doc source |
|---|---|---|---|
| 7  | `spring-to-micronaut`     | DOCS-CITED | https://docs.micronaut.io/4.7.x/guide/ |
| 8  | `micronaut-to-spring`     | DOCS-CITED | Same |
| 9  | `jakarta-to-micronaut`    | DOCS-CITED | Same |
| 10 | `micronaut-to-jakarta`    | DOCS-CITED | Same |
| 11 | `quarkus-to-micronaut`    | DOCS-CITED | Same |
| 12 | `micronaut-to-quarkus`    | DOCS-CITED | Same |

### New pairs — Helidon MP (6)

| # | Bundle | Provenance | Doc source |
|---|---|---|---|
| 13 | `spring-to-helidon`   | DOCS-CITED | https://helidon.io/docs/v4/mp/introduction |
| 14 | `helidon-to-spring`   | DOCS-CITED | Same |
| 15 | `jakarta-to-helidon`  | DOCS-CITED | Same. Closest source ↔ target (both CDI + JPA); expect highest pass rate among new pairs. |
| 16 | `helidon-to-jakarta`  | DOCS-CITED | Same |
| 17 | `quarkus-to-helidon`  | DOCS-CITED | Same |
| 18 | `helidon-to-quarkus`  | DOCS-CITED | Same |

### New pairs — Micronaut ↔ Helidon (2)

| # | Bundle | Provenance | Doc source |
|---|---|---|---|
| 19 | `micronaut-to-helidon` | DOCS-CITED | Both framework docs |
| 20 | `helidon-to-micronaut` | DOCS-CITED | Both framework docs |

### New pairs — Vert.x (10)

| # | Bundle | Provenance | Doc source |
|---|---|---|---|
| 21 | `spring-to-vertx`     | NEEDS-IMPL | https://vertx.io/docs/vertx-core/java/ |
| 22 | `vertx-to-spring`     | NEEDS-IMPL | Same |
| 23 | `jakarta-to-vertx`    | NEEDS-IMPL | Same |
| 24 | `vertx-to-jakarta`    | NEEDS-IMPL | Same |
| 25 | `quarkus-to-vertx`    | NEEDS-IMPL | Same |
| 26 | `vertx-to-quarkus`    | NEEDS-IMPL | Same |
| 27 | `micronaut-to-vertx`  | NEEDS-IMPL | Same |
| 28 | `vertx-to-micronaut`  | NEEDS-IMPL | Same |
| 29 | `helidon-to-vertx`    | NEEDS-IMPL | Same |
| 30 | `vertx-to-helidon`    | NEEDS-IMPL | Same |

**Why NEEDS-IMPL for all Vert.x pairs:** Vert.x's event-loop programming model
cannot be reached via API substitution alone. Every bundle in this group carries
an explicit "Architectural Rewrite Required" warning at the top of its
`SKILL.md`. Even with the warning, agents will almost certainly fail these
tasks until (a) paired implementations exist and (b) skills are re-derived
from real diffs.

---

## What "DOCS-CITED" does NOT prove

Skeptic disclosure:

1. **No agent runs have been executed** against these bundles yet. Table 2 / Table 7 / Table 8 of the paper cannot yet be extended for the new pairs.
2. **No paired implementations** exist for Micronaut / Helidon / Vert.x variants of the 34 SCARFBENCH apps. Until Phase 3 of the extension plan is done, "correctness" of these bundles is a hypothesis.
3. **Framework version drift risk.** Dependency coordinates were captured against the framework versions cited in the doc URLs above. If the benchmark pins different versions in Phase 3, the mappings need re-verification.
4. **Symmetry assumption is imperfect.** `micronaut-to-spring` is not literally the reverse of `spring-to-micronaut`. The bundles share content by design; per-direction pitfalls will surface only through empirical runs.

---

## Next steps to convert DOCS-CITED → GROUND-TRUTH

For each new pair (in priority order — see Phase 3 wave plan in the extension proposal):

1. Produce a paired implementation for at least 3 apps (pilot: `standalone`, `address-book`, `realworld`).
2. Extract diffs: `diff -r benchmark/<app>/<src> benchmark/<app>/<tgt>`.
3. Compare bundle mappings against the diffs; add missing entries, correct wrong ones.
4. Run the harness (paper §4) on all 5 agents × the 3 pilot apps.
5. Compare compile / deploy / test pass rates against `no-skills` baseline.
6. If skills lift ≥ 15 pp on compile OR ≥ 5 pp on test → promote to GROUND-TRUTH. Otherwise iterate.

---

## File map (generated)

```
scarfbench-evals/agents/
├── BUNDLE_REGISTRY.md           <-- this file
├── _validate_bundles.sh         <-- offline CI check (100 assertions, 0 fails)
├── gemini-with-skills/skills/
│   ├── _generate_skill_md.sh    <-- SKILL.md template generator
│   ├── _generate_references.py  <-- references/*.md generator
│   └── <30 bundle dirs>/{SKILL.md, references/{4 files}}
├── claude-with-skills/skills/   <-- byte-identical to gemini-with-skills
└── codex-with-skills/skills/    <-- byte-identical to gemini-with-skills
```

Regenerate all 24 new bundles from source:

```bash
cd scarfbench-evals/agents/gemini-with-skills/skills
./_generate_skill_md.sh          # writes 24 SKILL.md files
python3 _generate_references.py  # writes 72 reference files
# then mirror to claude-with-skills and codex-with-skills via the same
# copy pattern used to bootstrap them.
```

Validate:

```bash
cd scarfbench-evals/agents
./_validate_bundles.sh   # exits 0 iff all 100 checks pass
```
