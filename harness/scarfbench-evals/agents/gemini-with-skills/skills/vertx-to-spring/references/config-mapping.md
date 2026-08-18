# Configuration Mapping — Target: Spring Boot

Translate source-framework configuration to Spring Boot equivalents.

## Server

- HTTP port -> `server.port`
- Context path -> `server.servlet.context-path`

## Datasource and JPA

- JDBC URL/user/pass -> `spring.datasource.url|username|password`
- Schema generation -> `spring.jpa.hibernate.ddl-auto` (`none|update|create-drop`)
- SQL logging -> `spring.jpa.show-sql=true`

## Logging

- Package levels -> `logging.level.<package>=DEBUG|INFO|WARN|ERROR`
- Pattern -> `logging.pattern.console=...`

## Profiles

- Active profile -> `spring.profiles.active` or `SPRING_PROFILES_ACTIVE` env var
- Profile-specific overrides -> `application-<profile>.properties|yml`

## Notes

- Prefer `application.properties` or `application.yml`; choose one and stay consistent.
- Externalize secrets via environment variables or `SPRING_APPLICATION_JSON`.
