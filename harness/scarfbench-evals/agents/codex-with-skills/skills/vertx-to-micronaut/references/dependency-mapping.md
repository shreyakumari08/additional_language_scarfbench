# Dependency Mapping — Target: Micronaut

Use this as a starting point; verify against actual project behavior.
Reference: https://docs.micronaut.io/4.7.x/guide/#deps

## Maven Baseline

- Set `io.micronaut.platform:micronaut-parent` as parent POM (or import `micronaut-platform` BOM).
- Add `io.micronaut.maven:micronaut-maven-plugin` for build/dev/native support.
- The Micronaut annotation processor (`micronaut-inject-java`) MUST be on the
  annotation-processor path; missing it silently disables compile-time DI.

## Common Dependency Additions

- REST server: `io.micronaut:micronaut-http-server-netty`
- REST client: `io.micronaut:micronaut-http-client`
- JSON via Jackson: `io.micronaut.serde:micronaut-serde-jackson`
- Persistence (JPA): `io.micronaut.data:micronaut-data-hibernate-jpa` + `io.micronaut.sql:micronaut-jdbc-hikari` + JDBC driver
- Persistence (JDBC): `io.micronaut.data:micronaut-data-jdbc`
- Validation: `io.micronaut.validation:micronaut-validation`
- Security: `io.micronaut.security:micronaut-security-jwt` (or `-oauth2`)
- WebSocket: `io.micronaut:micronaut-websocket`
- Configuration: `io.micronaut:micronaut-runtime`
- Test: `io.micronaut.test:micronaut-test-junit5`

## Notes

- Prefer constructor injection; field injection works but is not idiomatic.
- Add `micronaut-management` for actuator-equivalent health/metrics endpoints.
- Bean introspection: annotate DTOs with `@Introspected` to avoid reflection at runtime.
