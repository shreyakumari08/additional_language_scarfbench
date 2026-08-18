# Dependency Mapping — Target: Helidon MP

Use this as a starting point; verify against actual project behavior.
Reference: https://helidon.io/docs/v4/mp/introduction

## Maven Baseline

- Import `io.helidon:helidon-bom` in `<dependencyManagement>` (or use `io.helidon.microprofile:helidon-microprofile` starter).
- Add `io.helidon.build-tools:helidon-maven-plugin` for fat-jar packaging.
- Use JAR packaging; runnable via `java -jar target/*.jar`.

## Common Dependency Additions

- Core MP: `io.helidon.microprofile.bundles:helidon-microprofile` (bundle of JAX-RS, CDI, Config, Health, Metrics)
- REST (JAX-RS explicit): `io.helidon.microprofile.jaxrs:helidon-microprofile-jaxrs`
- Persistence (JPA): `io.helidon.integrations.cdi:helidon-integrations-cdi-jpa` + JDBC driver + Hibernate/EclipseLink
- CDI: `io.helidon.microprofile.cdi:helidon-microprofile-cdi`
- Validation: `io.helidon.microprofile.bean-validation:helidon-microprofile-bean-validation`
- Security: `io.helidon.microprofile.security:helidon-microprofile-security` + JWT/OIDC providers
- WebSocket: `io.helidon.microprofile.websocket:helidon-microprofile-websocket`
- MicroProfile Config/RestClient/Metrics: pulled in via bundle
- Test: `io.helidon.microprofile.testing:helidon-microprofile-testing-junit5`

## Notes

- Jandex indexing (`jandex-maven-plugin`) improves startup and is recommended.
- Helidon MP is CDI-based (Weld); annotations largely match Jakarta EE.
