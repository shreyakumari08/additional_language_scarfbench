---
name: migrate-spring-to-vertx
description: Migrate Java applications from Spring to Vert.x with one-shot execution. Use when converting Spring projects to Vert.x, or when asked to perform Spring-to-Vert.x dependency, configuration, annotation, and build migration with compile validation and migration logging.
---

# Spring to Vert.x Migration

Execute the migration autonomously in one run. Do not ask follow-up questions unless blocked by missing or unreadable files.


## Warning: Architectural Rewrite Required

Migration to Vert.x is NOT an API substitution. The application must be re-architected around an event loop:

- Blocking calls (JDBC, JPA, servlet I/O) must become `Future<T>`-returning
- Services become verticles (`AbstractVerticle`); no CDI-style DI is provided by the framework
- Persistence layer must be rewritten to use `vertx-sql-client` or a reactive driver; JPA/EntityManager is not portable
- Expect >70% of source code to change; oracle-visible behavior (routes, payloads) must still be preserved

## Operating Contract

Follow these constraints for every run:

- Work only inside the provided project root.
- Prioritize successful compilation over stylistic refactors.
- Preserve business behavior while replacing framework integrations.
- Preserve Vert.x event-loop model: NEVER block the event loop. All I/O becomes Future/Uni-returning. Verticles are the composition unit.
- Keep a chronological migration log in `migration-artifacts/MONOLOUGE.log.md` with ISO8601 UTC timestamps and severity levels: `info`, `warning`, `error`.
- Persist verbose intermediate artifacts under `migration-artifacts/` for every step, including: thinking/reasoning notes, every tool/command call with inputs, raw tool outputs, interpretation of outputs, and explicit next-step decisions.

## Required Workflow

Run these steps in order and log each step outcome in `migration-artifacts/MONOLOUGE.log.md`.

1. Inspect project structure.
2. Detect build system and framework usage.
3. Migrate dependencies and plugins.
4. Migrate framework configuration.
5. Refactor framework-bound source code.
6. Compile and fix errors until build succeeds or no safe fix remains.
7. Produce a final migration report including file changes, chronological log, full `migration-artifacts/MONOLOUGE.log.md`, and unresolved issues.

## Step 1: Inspect Project Structure

- Identify build files (`pom.xml`, `build.gradle`, `settings.gradle`, wrappers).
- Detect module layout (single module vs multi-module).
- Enumerate framework-bound entrypoints and layers relevant to Spring:
  - HTTP/REST resources
  - DI beans/producers/scopes
  - Persistence entities and repositories
  - Spring-specific runtime config and lifecycle hooks
- Record findings in `migration-artifacts/MONOLOUGE.log.md`.

## Step 2: Detect Migration Scope

- Locate Spring dependencies and plugins in build files.
- Locate Spring-specific configuration files (properties/YAML/XML).
- Locate Spring API imports/annotations in Java/Kotlin sources.
- Use targeted search (`rg`) and log counts/locations before editing.

## Step 3: Migrate Dependencies and Build

- Replace Spring dependencies with Vert.x equivalents per `references/dependency-mapping.md`.
- Update or replace the build plugin driving packaging.
- Keep non-framework libraries unless incompatible.

For common mappings, read:

- `references/dependency-mapping.md`

Validation:

- Resolve dependencies by running a compile/package command before code refactors when feasible.
- If this early build fails due to known source incompatibility, continue and log why.

## Step 4: Migrate Configuration

- Convert Spring configuration keys and conventions to Vert.x equivalents per `references/config-mapping.md`.
- Preserve ports, datasource settings, profile intent, and feature flags.
- Keep application-level config in project conventions and runtime-level config where the target framework expects it.

For common property mappings, read:

- `references/config-mapping.md`

Validation:

- Re-check configuration syntax and required values before compile.

## Step 5: Refactor Source Code

- Replace Spring-specific APIs, annotations, and idioms with Vert.x equivalents per `references/code-mapping.md`.
- Preserve domain models, DTOs, and business logic as framework-neutral assets.
- Ensure bootstrapping and packaging are compatible with the Vert.x deployment model.

For common annotation and API mappings, read:

- `references/code-mapping.md`

Validation:

- After significant refactors, run a compile/build.
- Fix compile errors in small batches and log each resolution.

## Step 6: Compile and Iterate

Use commands that write local caches to project-local directories:

- Maven: `mvn -q -Dmaven.repo.local=.m2repo clean package`
- Gradle: `./gradlew -g .gradle clean build`

If build fails:

- Capture the exact failing symbols/classes/modules.
- Apply minimal, safe fix.
- Rebuild and repeat.
- If blocked, record clear mitigation steps for manual follow-up.

## Step 7: Logging and Report Requirements

For monologue and intermediate artifact capture requirements, read:

- `references/MONOLOUGE.md`

- `migration-artifacts/MONOLOUGE.log.md` is the only required migration log file and single source of truth.

`migration-artifacts/MONOLOUGE.log.md` format:

- Use one entry per action or corrective step.
- Include timestamp, severity, title, and details.
- For errors include:
  - failing file/area
  - root cause
  - attempted fix
  - final outcome

Final response format:

1. `Migration Summary`
2. `File Tree` with modified/added/removed files and short purpose notes
3. `Step-by-Step Log` in chronological order
4. `MONOLOUGE.log.md Contents` full file in fenced code block
5. `Error Handling` with counts, blockers, mitigations, and manual intervention flag

## Execution Policy

- Do not stop at partial edits when a safe next fix exists.
- Do not claim success without a compile attempt.
- If full migration is not possible, provide precise blockers and next actions.
