# Configuration Mapping — Target: Helidon MP

Translate source-framework configuration to Helidon MP equivalents.
Reference: https://helidon.io/docs/v4/mp/config/introduction

## Server

- HTTP port -> `server.port` (in `META-INF/microprofile-config.properties`)
- Host binding -> `server.host`
- Context root -> configured per JAX-RS application

## Datasource and JPA

- JDBC URL/user/pass -> `javax.sql.DataSource.<name>.dataSource.<property>` OR use CDI-produced DataSource
- JPA properties -> `persistence.xml` at `META-INF/persistence.xml`

## Logging

- Package levels -> `logging.properties` (java.util.logging format)
- Example: `com.example.level = FINE`

## Profiles / Environment

- No native profile system; use MicroProfile Config with environment/system property overrides
- Environment vars automatically mapped (uppercase, underscores)

## Notes

- Helidon MP config is based on MicroProfile Config 3.x.
- Ordered sources: system properties -> environment -> microprofile-config.properties -> application.yaml.
- Prefer `microprofile-config.properties` for portability across MP servers.
