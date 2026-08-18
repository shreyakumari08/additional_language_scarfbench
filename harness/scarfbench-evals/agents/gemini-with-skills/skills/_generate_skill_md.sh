#!/usr/bin/env bash
# Generates SKILL.md for the 24 new bundles by templating the existing
# spring-to-quarkus format. The 6 original bundles (spring/jakarta/quarkus
# pairs) are not touched.
#
# Evidence for the template: it mirrors
#   scarfbench-evals/agents/gemini-with-skills/skills/spring-to-quarkus/SKILL.md
# which the paper (SCARFBENCH §E.3) confirms is the canonical bundle format.

set -eo pipefail
cd "$(dirname "$0")"

display_name() {
  case "$1" in
    spring)    echo "Spring" ;;
    jakarta)   echo "Jakarta EE (OpenLiberty)" ;;
    quarkus)   echo "Quarkus" ;;
    micronaut) echo "Micronaut" ;;
    helidon)   echo "Helidon MP" ;;
    vertx)     echo "Vert.x" ;;
    *)         echo "UNKNOWN"; return 1 ;;
  esac
}

runtime_note() {
  case "$1" in
    spring)    echo "Preserve Spring Boot embedded runtime assumptions and executable-jar packaging." ;;
    jakarta)   echo "Target OpenLiberty runtime assumptions for Jakarta EE packaging, APIs, and configuration." ;;
    quarkus)   echo "Preserve Quarkus build-time augmentation, BOM-managed dependencies, and native-image compatibility where declared." ;;
    micronaut) echo "Preserve Micronaut compile-time DI, AOT annotation processing, and Netty runtime." ;;
    helidon)   echo "Preserve Helidon MP MicroProfile+CDI stack and fat-jar packaging with helidon-maven-plugin." ;;
    vertx)     echo "Preserve Vert.x event-loop model: NEVER block the event loop. All I/O becomes Future/Uni-returning. Verticles are the composition unit." ;;
    *)         return 1 ;;
  esac
}

PAIRS="
spring-to-micronaut micronaut-to-spring
jakarta-to-micronaut micronaut-to-jakarta
quarkus-to-micronaut micronaut-to-quarkus
spring-to-helidon helidon-to-spring
jakarta-to-helidon helidon-to-jakarta
quarkus-to-helidon helidon-to-quarkus
micronaut-to-helidon helidon-to-micronaut
spring-to-vertx vertx-to-spring
jakarta-to-vertx vertx-to-jakarta
quarkus-to-vertx vertx-to-quarkus
micronaut-to-vertx vertx-to-micronaut
helidon-to-vertx vertx-to-helidon
"

generated=0
for pair in $PAIRS; do
  src="${pair%-to-*}"
  tgt="${pair#*-to-}"
  src_name="$(display_name "$src")"
  tgt_name="$(display_name "$tgt")"
  tgt_note="$(runtime_note "$tgt")"

  arch_warning=""
  if [ "$tgt" = "vertx" ]; then
    arch_warning="

## Warning: Architectural Rewrite Required

Migration to Vert.x is NOT an API substitution. The application must be re-architected around an event loop:

- Blocking calls (JDBC, JPA, servlet I/O) must become \`Future<T>\`-returning
- Services become verticles (\`AbstractVerticle\`); no CDI-style DI is provided by the framework
- Persistence layer must be rewritten to use \`vertx-sql-client\` or a reactive driver; JPA/EntityManager is not portable
- Expect >70% of source code to change; oracle-visible behavior (routes, payloads) must still be preserved
"
  fi

  cat > "$pair/SKILL.md" <<EOF
---
name: migrate-${src}-to-${tgt}
description: Migrate Java applications from ${src_name} to ${tgt_name} with one-shot execution. Use when converting ${src_name} projects to ${tgt_name}, or when asked to perform ${src_name}-to-${tgt_name} dependency, configuration, annotation, and build migration with compile validation and migration logging.
---

# ${src_name} to ${tgt_name} Migration

Execute the migration autonomously in one run. Do not ask follow-up questions unless blocked by missing or unreadable files.
${arch_warning}
## Operating Contract

Follow these constraints for every run:

- Work only inside the provided project root.
- Prioritize successful compilation over stylistic refactors.
- Preserve business behavior while replacing framework integrations.
- ${tgt_note}
- Keep a chronological migration log in \`migration-artifacts/MONOLOUGE.log.md\` with ISO8601 UTC timestamps and severity levels: \`info\`, \`warning\`, \`error\`.
- Persist verbose intermediate artifacts under \`migration-artifacts/\` for every step, including: thinking/reasoning notes, every tool/command call with inputs, raw tool outputs, interpretation of outputs, and explicit next-step decisions.

## Required Workflow

Run these steps in order and log each step outcome in \`migration-artifacts/MONOLOUGE.log.md\`.

1. Inspect project structure.
2. Detect build system and framework usage.
3. Migrate dependencies and plugins.
4. Migrate framework configuration.
5. Refactor framework-bound source code.
6. Compile and fix errors until build succeeds or no safe fix remains.
7. Produce a final migration report including file changes, chronological log, full \`migration-artifacts/MONOLOUGE.log.md\`, and unresolved issues.

## Step 1: Inspect Project Structure

- Identify build files (\`pom.xml\`, \`build.gradle\`, \`settings.gradle\`, wrappers).
- Detect module layout (single module vs multi-module).
- Enumerate framework-bound entrypoints and layers relevant to ${src_name}:
  - HTTP/REST resources
  - DI beans/producers/scopes
  - Persistence entities and repositories
  - ${src_name}-specific runtime config and lifecycle hooks
- Record findings in \`migration-artifacts/MONOLOUGE.log.md\`.

## Step 2: Detect Migration Scope

- Locate ${src_name} dependencies and plugins in build files.
- Locate ${src_name}-specific configuration files (properties/YAML/XML).
- Locate ${src_name} API imports/annotations in Java/Kotlin sources.
- Use targeted search (\`rg\`) and log counts/locations before editing.

## Step 3: Migrate Dependencies and Build

- Replace ${src_name} dependencies with ${tgt_name} equivalents per \`references/dependency-mapping.md\`.
- Update or replace the build plugin driving packaging.
- Keep non-framework libraries unless incompatible.

For common mappings, read:

- \`references/dependency-mapping.md\`

Validation:

- Resolve dependencies by running a compile/package command before code refactors when feasible.
- If this early build fails due to known source incompatibility, continue and log why.

## Step 4: Migrate Configuration

- Convert ${src_name} configuration keys and conventions to ${tgt_name} equivalents per \`references/config-mapping.md\`.
- Preserve ports, datasource settings, profile intent, and feature flags.
- Keep application-level config in project conventions and runtime-level config where the target framework expects it.

For common property mappings, read:

- \`references/config-mapping.md\`

Validation:

- Re-check configuration syntax and required values before compile.

## Step 5: Refactor Source Code

- Replace ${src_name}-specific APIs, annotations, and idioms with ${tgt_name} equivalents per \`references/code-mapping.md\`.
- Preserve domain models, DTOs, and business logic as framework-neutral assets.
- Ensure bootstrapping and packaging are compatible with the ${tgt_name} deployment model.

For common annotation and API mappings, read:

- \`references/code-mapping.md\`

Validation:

- After significant refactors, run a compile/build.
- Fix compile errors in small batches and log each resolution.

## Step 6: Compile and Iterate

Use commands that write local caches to project-local directories:

- Maven: \`mvn -q -Dmaven.repo.local=.m2repo clean package\`
- Gradle: \`./gradlew -g .gradle clean build\`

If build fails:

- Capture the exact failing symbols/classes/modules.
- Apply minimal, safe fix.
- Rebuild and repeat.
- If blocked, record clear mitigation steps for manual follow-up.

## Step 7: Logging and Report Requirements

For monologue and intermediate artifact capture requirements, read:

- \`references/MONOLOUGE.md\`

- \`migration-artifacts/MONOLOUGE.log.md\` is the only required migration log file and single source of truth.

\`migration-artifacts/MONOLOUGE.log.md\` format:

- Use one entry per action or corrective step.
- Include timestamp, severity, title, and details.
- For errors include:
  - failing file/area
  - root cause
  - attempted fix
  - final outcome

Final response format:

1. \`Migration Summary\`
2. \`File Tree\` with modified/added/removed files and short purpose notes
3. \`Step-by-Step Log\` in chronological order
4. \`MONOLOUGE.log.md Contents\` full file in fenced code block
5. \`Error Handling\` with counts, blockers, mitigations, and manual intervention flag

## Execution Policy

- Do not stop at partial edits when a safe next fix exists.
- Do not claim success without a compile attempt.
- If full migration is not possible, provide precise blockers and next actions.
EOF
  generated=$((generated + 1))
done

echo "Generated ${generated} SKILL.md files."
