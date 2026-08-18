# Dependency Mapping — Target: Jakarta EE (OpenLiberty)

Use this as a starting point; verify against actual project behavior.

## Maven Baseline

- Use WAR packaging.
- Add `io.openliberty.tools:liberty-maven-plugin` for build/deploy.
- Depend on Jakarta APIs at `provided` scope; runtime is supplied by OpenLiberty.

## Common Dependency Additions

- REST: `jakarta.ws.rs:jakarta.ws.rs-api` (provided)
- CDI: `jakarta.enterprise:jakarta.enterprise.cdi-api` (provided)
- Persistence: `jakarta.persistence:jakarta.persistence-api` (provided)
- Validation: `jakarta.validation:jakarta.validation-api` (provided)
- WebSocket: `jakarta.websocket:jakarta.websocket-api` (provided)
- Servlet: `jakarta.servlet:jakarta.servlet-api` (provided)
- MicroProfile Config/Rest Client where used: `org.eclipse.microprofile:microprofile`

## Notes

- Add `io.openliberty.tools:liberty-maven-plugin` where build/deploy flow expects it.
- Avoid packaging APIs already provided by OpenLiberty runtime.
- Configure enabled features in `src/main/liberty/config/server.xml`.
