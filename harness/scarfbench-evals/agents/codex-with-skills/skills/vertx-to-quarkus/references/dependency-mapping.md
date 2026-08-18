# Dependency Mapping — Target: Quarkus

Use this as a starting point; verify against actual project behavior.

## Maven Baseline

- Import `io.quarkus.platform:quarkus-bom` in `<dependencyManagement>`.
- Add `io.quarkus:quarkus-maven-plugin` (build-time augmentation).
- Prefer JAR packaging; runnable via `java -jar target/quarkus-app/quarkus-run.jar`.

## Common Dependency Additions

- REST: `io.quarkus:quarkus-rest` (RESTEasy Reactive) or `io.quarkus:quarkus-resteasy` (classic)
- REST JSON: `io.quarkus:quarkus-rest-jackson`
- Persistence: `io.quarkus:quarkus-hibernate-orm` (JPA) or `io.quarkus:quarkus-hibernate-orm-panache` (active record)
- JDBC drivers: `io.quarkus:quarkus-jdbc-postgresql`, `-h2`, `-mysql`, etc.
- Validation: `io.quarkus:quarkus-hibernate-validator`
- Security: `io.quarkus:quarkus-security` + feature extensions (JWT/OIDC)
- WebSocket: `io.quarkus:quarkus-websockets` (or `quarkus-websockets-next`)
- Messaging: `io.quarkus:quarkus-smallrye-reactive-messaging-*`
- Test: `io.quarkus:quarkus-junit5`, `io.rest-assured:rest-assured`

## Notes

- Prefer Quarkus extension artifacts over manually composing transitive stacks.
- Panache repositories are optional; plain `EntityManager` also works.
