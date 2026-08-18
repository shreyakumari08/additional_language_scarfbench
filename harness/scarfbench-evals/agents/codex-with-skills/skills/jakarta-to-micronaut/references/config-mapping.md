# Configuration Mapping — Target: Micronaut

Translate source-framework configuration to Micronaut equivalents.
Reference: https://docs.micronaut.io/4.7.x/guide/#config

## Server

- HTTP port -> `micronaut.server.port`
- Context path -> `micronaut.server.context-path`

## Datasource and JPA

- JDBC URL/user/pass -> `datasources.default.url|username|password`
- Dialect -> `jpa.default.properties.hibernate.dialect`
- Schema generation -> `jpa.default.properties.hibernate.hbm2ddl.auto`
- SQL logging -> `jpa.default.properties.hibernate.show_sql=true`

## Logging

- Package levels -> `logger.levels.<package>=DEBUG|INFO|WARN|ERROR` (in `logback.xml`, since Micronaut uses SLF4J+Logback by default)
- Runtime logger changes -> `/loggers` management endpoint (with `micronaut-management`)

## Profiles / Environments

- Active environments -> `micronaut.environments` or `MICRONAUT_ENVIRONMENTS` env var
- Environment-specific files -> `application-<env>.yml`
- Property source ordering documented at `https://docs.micronaut.io/4.7.x/guide/#propertySource`

## Notes

- `application.yml` is idiomatic; `application.properties` also supported.
- Externalize secrets via environment variables (`MICRONAUT_` prefix auto-mapped to keys).
