# Dependency Mapping — Target: Spring Boot

Use this as a starting point; verify against actual project behavior.

## Maven Baseline

- Set `spring-boot-starter-parent` as parent POM (or import `spring-boot-dependencies` BOM).
- Add `spring-boot-maven-plugin` for executable-jar packaging.
- Keep test plugins and compiler plugins if compatible.

## Common Dependency Additions

- REST/web: `org.springframework.boot:spring-boot-starter-web`
- JSON via Jackson is transitive from starter-web (no extra dep)
- Persistence: `org.springframework.boot:spring-boot-starter-data-jpa` + JDBC driver
- Validation: `org.springframework.boot:spring-boot-starter-validation`
- Security: `org.springframework.boot:spring-boot-starter-security`
- WebSocket: `org.springframework.boot:spring-boot-starter-websocket`
- Actuator/health: `org.springframework.boot:spring-boot-starter-actuator`
- Test: `org.springframework.boot:spring-boot-starter-test`

## Notes

- Prefer Spring Boot starters over hand-picked transitive stacks.
- Devtools is optional; do not add unless project requires hot reload.
