# Dependency Mapping — Target: Vert.x

Use this as a starting point; verify against actual project behavior.
Reference: https://vertx.io/docs/vertx-core/java/

## Maven Baseline

- Import `io.vertx:vertx-stack-depchain` in `<dependencyManagement>`.
- Use `maven-shade-plugin` to build a fat jar; main class is a `Launcher` or main verticle.
- JAR packaging; runnable via `java -jar target/*-fat.jar`.

## Common Dependency Additions

- Core: `io.vertx:vertx-core`
- HTTP router: `io.vertx:vertx-web`
- Body/multipart handling: transitive from vertx-web
- JSON: transitive (Vert.x uses Jackson internally)
- Persistence (reactive): `io.vertx:vertx-pg-client`, `io.vertx:vertx-mysql-client`, `io.vertx:vertx-jdbc-client`
- Config: `io.vertx:vertx-config`
- Validation: `io.vertx:vertx-web-validation` (OpenAPI-driven)
- Auth: `io.vertx:vertx-auth-common` + provider (`vertx-auth-jwt`, `-oauth2`)
- WebSocket: built into vertx-core/vertx-web
- Scheduling: `io.vertx:vertx-core` (`vertx.setPeriodic`) or `vertx-cron`
- Test: `io.vertx:vertx-junit5`

## Notes

- Vert.x is NOT a JPA runtime. Do not add Hibernate-based persistence unless
  paired with Hibernate Reactive (`io.vertx:vertx-hibernate-reactive`), which
  changes the API surface significantly.
- Blocking calls (JDBC, EntityManager) MUST run under `vertx.executeBlocking()`
  or be replaced with reactive equivalents.
