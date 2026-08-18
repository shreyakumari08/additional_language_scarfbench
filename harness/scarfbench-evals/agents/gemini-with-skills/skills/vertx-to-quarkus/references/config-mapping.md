# Configuration Mapping — Target: Quarkus

Translate source-framework configuration to Quarkus equivalents.

## Server

- HTTP port -> `quarkus.http.port`
- Root/context path -> `quarkus.http.root-path`

## Datasource and JPA

- JDBC URL/user/pass -> `quarkus.datasource.jdbc.url|username|password`
- Schema generation -> `quarkus.hibernate-orm.database.generation` (`none|create|drop-and-create|update`)
- SQL logging -> `quarkus.hibernate-orm.log.sql=true`

## Logging

- Package levels -> `quarkus.log.category."<package>".level`
- Console format -> `quarkus.log.console.format`

## Profiles

- Profile-scoped keys -> `%dev`, `%test`, `%prod` prefix
- Active profile -> `-Dquarkus.profile=<name>` or `QUARKUS_PROFILE` env var

## Notes

- `application.properties` is the primary config file (YAML supported via extension).
- Native-image constants must be resolvable at build time; avoid runtime-only injection where declared.
