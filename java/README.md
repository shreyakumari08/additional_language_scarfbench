# Java Benchmark

Directory tree: `benchmark/{business_domain,dependency_injection,infrastructure,persistence,presentation,whole_applications}/<app>/<framework>/`.

## Frameworks

| Origin | Frameworks | Version |
|---|---|---|
| **Paper originals** | Spring Boot | 3.x |
| | Quarkus | 3.15 |
| | Jakarta EE / OpenLiberty | 10 |
| **Added in this repo** | Micronaut | 4.7.4 |
| | Helidon MP | 4.1.4 |
| | Vert.x | 4.5.11 |

All six frameworks share the same per-app skeleton (`.dockerignore`,
`Dockerfile`, `mvnw`, `pom.xml`, `test.sh`, `src/main/**`, `src/test/**`)
so any migration path between them is a valid Original-Harness task.

See [`MICRONAUT_HELIDON_VERTX_IMPLEMENTATION.md`](./MICRONAUT_HELIDON_VERTX_IMPLEMENTATION.md)
for the convention and the exemplar built for `business_domain/standalone/micronaut/`.

## Migration paths (examples)

Any framework pair, both directions:

- `spring ⇄ quarkus`
- `jakarta → spring`, `spring → jakarta`
- `spring → micronaut`, `quarkus → helidon`, `jakarta → vertx`, …

## Run a task

From the repo root:

```bash
./run-task.sh counter spring quarkus
./run-task.sh helloservice spring micronaut business_domain
```

## Prereqs

- Java 21 (IBM Semeru or Temurin)
- Maven wrapper is bundled per app (`./mvnw`)
- Docker
