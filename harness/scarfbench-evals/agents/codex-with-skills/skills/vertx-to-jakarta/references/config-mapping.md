# Configuration Mapping — Target: Jakarta EE (OpenLiberty)

Translate source-framework configuration to OpenLiberty/Jakarta EE equivalents.

## Server

- HTTP port -> `<httpEndpoint httpPort="9080">` in `src/main/liberty/config/server.xml`
- Context root -> `<webApplication contextRoot="/">`

## Datasource and JPA

- JDBC URL/user/pass -> `<dataSource>` element with `<properties.postgresql url="..."/>` in server.xml
- Schema generation -> JPA provider property in `persistence.xml` (`javax.persistence.schema-generation.database.action`)
- SQL logging -> provider-specific (Hibernate: `hibernate.show_sql`) in `persistence.xml`

## Logging

- Package levels -> `<logging traceSpecification="com.example.*=all"/>` in server.xml
- Standard output via `<logging consoleLogLevel="INFO"/>`

## Profiles / Environment

- No native profile system; use OpenLiberty variables in server.xml
- Override via environment: `server.env` file with `KEY=value`

## Notes

- Put runtime concerns in `server.xml`, application concerns in `META-INF/microprofile-config.properties`.
- Enable required features (`<feature>jakartaee-10.0</feature>`, `<feature>microProfile-6.1</feature>`).
