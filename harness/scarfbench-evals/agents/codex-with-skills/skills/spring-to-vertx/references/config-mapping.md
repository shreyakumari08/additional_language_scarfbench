# Configuration Mapping — Target: Vert.x

Translate source-framework configuration to Vert.x equivalents.
Reference: https://vertx.io/docs/vertx-config/java/

## Server

- HTTP port -> passed to `HttpServer.listen(port)` in code; NOT a magic config key
- Bind address -> passed to `HttpServer.listen(port, host)`
- Vert.x does not read `application.properties`; it uses `ConfigRetriever`

## Datasource

- Reactive client options via `PgConnectOptions`/`MySQLConnectOptions` (host, port, database, user, password)
- Pool sizing via `PoolOptions.setMaxSize()`
- Configure inline in verticle `start()` or from `ConfigRetriever`

## Logging

- Vert.x uses SLF4J by default; configure via `logback.xml`
- Enable JUL/Log4j via system property `vertx.logger-delegate-factory-class-name`

## Configuration Retrieval

- Standard: `ConfigRetriever.create(vertx, ConfigRetrieverOptions)`
- Supported stores: JSON file, YAML file (with `vertx-config-yaml`), env variables, system properties, HTTP endpoint, Kubernetes ConfigMap
- Best practice: load config in `Launcher` or main verticle before deploying app verticles

## Notes

- Vert.x has no "profile" system. Encode environment selection in the config file loaded.
- All configuration is JSON-shaped internally (`JsonObject`).
