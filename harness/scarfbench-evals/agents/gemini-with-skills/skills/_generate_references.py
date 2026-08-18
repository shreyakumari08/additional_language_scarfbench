#!/usr/bin/env python3
"""
Generates the three reference files (dependency-mapping.md, config-mapping.md,
code-mapping.md) for the 24 new skill bundles.

The 6 original bundles (spring/jakarta/quarkus pairs) are NOT touched.

Content evidence:
- Existing spring-to-quarkus, quarkus-to-jakarta bundles served as format anchors.
- Framework capability data derived from official docs:
    * Micronaut:  https://docs.micronaut.io/4.7.x/guide/
    * Helidon MP: https://helidon.io/docs/v4/mp/introduction
    * Vert.x:     https://vertx.io/docs/vertx-core/java/
- Cross-framework mappings validated against paired implementations in
  benchmark/whole_applications/petclinic/{spring,jakarta,quarkus}/pom.xml
  where the existing 3 frameworks overlap the new 3.

The bundles follow the style set by the paper (SCARFBENCH §E.3): terse,
"starting point; verify against actual project behavior" tone. Not exhaustive.
"""

from pathlib import Path

BUNDLES_ROOT = Path(__file__).parent

# ---------- Framework catalogue (target-facing) ----------
# For each framework, we describe what to ADD when it is the TARGET.

TARGET_DEPS = {
    "spring": """# Dependency Mapping — Target: Spring Boot

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
""",

    "jakarta": """# Dependency Mapping — Target: Jakarta EE (OpenLiberty)

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
""",

    "quarkus": """# Dependency Mapping — Target: Quarkus

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
""",

    "micronaut": """# Dependency Mapping — Target: Micronaut

Use this as a starting point; verify against actual project behavior.
Reference: https://docs.micronaut.io/4.7.x/guide/#deps

## Maven Baseline

- Set `io.micronaut.platform:micronaut-parent` as parent POM (or import `micronaut-platform` BOM).
- Add `io.micronaut.maven:micronaut-maven-plugin` for build/dev/native support.
- The Micronaut annotation processor (`micronaut-inject-java`) MUST be on the
  annotation-processor path; missing it silently disables compile-time DI.

## Common Dependency Additions

- REST server: `io.micronaut:micronaut-http-server-netty`
- REST client: `io.micronaut:micronaut-http-client`
- JSON via Jackson: `io.micronaut.serde:micronaut-serde-jackson`
- Persistence (JPA): `io.micronaut.data:micronaut-data-hibernate-jpa` + `io.micronaut.sql:micronaut-jdbc-hikari` + JDBC driver
- Persistence (JDBC): `io.micronaut.data:micronaut-data-jdbc`
- Validation: `io.micronaut.validation:micronaut-validation`
- Security: `io.micronaut.security:micronaut-security-jwt` (or `-oauth2`)
- WebSocket: `io.micronaut:micronaut-websocket`
- Configuration: `io.micronaut:micronaut-runtime`
- Test: `io.micronaut.test:micronaut-test-junit5`

## Notes

- Prefer constructor injection; field injection works but is not idiomatic.
- Add `micronaut-management` for actuator-equivalent health/metrics endpoints.
- Bean introspection: annotate DTOs with `@Introspected` to avoid reflection at runtime.
""",

    "helidon": """# Dependency Mapping — Target: Helidon MP

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
""",

    "vertx": """# Dependency Mapping — Target: Vert.x

Use this as a starting point; verify against actual project behavior.
Reference: https://vertx.io/docs/vertx-core/java/

## Maven Baseline

- Import `io.vertx:vertx-stack-depchain` in `<dependencyManagement>`.
- Use `maven-shade-plugin` to build a fat jar; main class is a `Launcher` or main verticle.
- JAR packaging; runnable via `java -jar target/*-fat.jar`.

## Common Dependency Additions

- Core: `io.vertx:vertx-core`
- HTTP router: `io.vertx:vertx-web`
- Body/multipart handling: transitive from vertx-web
- JSON: transitive (Vert.x uses Jackson internally)
- Persistence (reactive): `io.vertx:vertx-pg-client`, `io.vertx:vertx-mysql-client`, `io.vertx:vertx-jdbc-client`
- Config: `io.vertx:vertx-config`
- Validation: `io.vertx:vertx-web-validation` (OpenAPI-driven)
- Auth: `io.vertx:vertx-auth-common` + provider (`vertx-auth-jwt`, `-oauth2`)
- WebSocket: built into vertx-core/vertx-web
- Scheduling: `io.vertx:vertx-core` (`vertx.setPeriodic`) or `vertx-cron`
- Test: `io.vertx:vertx-junit5`

## Notes

- Vert.x is NOT a JPA runtime. Do not add Hibernate-based persistence unless
  paired with Hibernate Reactive (`io.vertx:vertx-hibernate-reactive`), which
  changes the API surface significantly.
- Blocking calls (JDBC, EntityManager) MUST run under `vertx.executeBlocking()`
  or be replaced with reactive equivalents.
""",
}

TARGET_CONFIG = {
    "spring": """# Configuration Mapping — Target: Spring Boot

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
""",

    "jakarta": """# Configuration Mapping — Target: Jakarta EE (OpenLiberty)

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
""",

    "quarkus": """# Configuration Mapping — Target: Quarkus

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
""",

    "micronaut": """# Configuration Mapping — Target: Micronaut

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
""",

    "helidon": """# Configuration Mapping — Target: Helidon MP

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
""",

    "vertx": """# Configuration Mapping — Target: Vert.x

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
""",
}

# Code mapping keyed by (source, target). We define groups because many
# mappings are symmetric or reusable within a target framework family.

CODE_MAPPING = {}

# Helpers to build code-mapping content by target family.

def code_map(source: str, target: str) -> str:
    """Emit code-mapping.md for a given directed pair."""
    src_name = DISPLAY[source]
    tgt_name = DISPLAY[target]

    header = f"""# Code Mapping — {src_name} to {tgt_name}

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.
"""

    # ---------- DI / scopes ----------
    di_source = DI_SOURCE.get(source, "")
    di_target = DI_TARGET.get(target, "")

    di = f"""

## Injection and Scopes

Source ({src_name}):
{di_source}

Target ({tgt_name}):
{di_target}
"""

    # ---------- REST endpoints ----------
    rest_source = REST_SOURCE.get(source, "")
    rest_target = REST_TARGET.get(target, "")

    rest = f"""

## Web / REST Endpoints

Source ({src_name}):
{rest_source}

Target ({tgt_name}):
{rest_target}
"""

    # ---------- Persistence ----------
    persist = f"""

## Persistence and Transactions

Source ({src_name}):
{PERSIST_SOURCE.get(source, '- (see project code)')}

Target ({tgt_name}):
{PERSIST_TARGET.get(target, '- (see project code)')}
"""

    # ---------- Bootstrapping ----------
    boot = f"""

## Bootstrapping / Entry Point

- {src_name}: {BOOT_SOURCE.get(source, '(see project code)')}
- {tgt_name}: {BOOT_TARGET.get(target, '(see project code)')}
"""

    # ---------- Pitfalls (target-specific) ----------
    pitfalls = TARGET_PITFALLS.get(target, "")
    if pitfalls:
        pitfalls = f"\n\n## Common Pitfalls ({tgt_name})\n{pitfalls}\n"

    return header + di + rest + persist + boot + pitfalls


DISPLAY = {
    "spring": "Spring",
    "jakarta": "Jakarta EE",
    "quarkus": "Quarkus",
    "micronaut": "Micronaut",
    "helidon": "Helidon MP",
    "vertx": "Vert.x",
}

DI_SOURCE = {
    "spring":    "- `@Service`, `@Component`, `@Repository`, `@Configuration`\n- `@Autowired` (field or constructor)\n- `@Qualifier` for disambiguation",
    "jakarta":   "- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`\n- `@Inject` (JSR-330)\n- `@Named`, `@Qualifier` for disambiguation",
    "quarkus":   "- CDI: `@ApplicationScoped`, `@RequestScoped`\n- `@Inject`\n- Quarkus adds `@Startup`, `@Scheduled`",
    "micronaut": "- `@Singleton`, `@Prototype`, `@RequestScope`\n- `@Inject` (constructor preferred)\n- `@Named`, `@Qualifier` for disambiguation",
    "helidon":   "- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`\n- `@Inject` (JSR-330)\n- `@Named`",
    "vertx":     "- No DI framework by default\n- Composition via `AbstractVerticle` subclasses and constructor arguments\n- Optional integration: Guice or hand-rolled factories",
}
DI_TARGET = DI_SOURCE  # same shape

REST_SOURCE = {
    "spring":    "- `@RestController` on class\n- `@RequestMapping`, `@GetMapping`, `@PostMapping`\n- `@PathVariable`, `@RequestParam`, `@RequestBody`\n- Return type: DTO (auto-serialized) or `ResponseEntity<T>`",
    "jakarta":   "- `@Path` on class\n- `@GET`, `@POST`, `@PUT`, `@DELETE`\n- `@PathParam`, `@QueryParam`, entity body via method parameter\n- Return type: DTO or `jakarta.ws.rs.core.Response`",
    "quarkus":   "- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)\n- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)\n- Return DTO or `Response`",
    "micronaut": "- `@Controller` on class\n- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)\n- `@PathVariable`, `@QueryValue`, `@Body`\n- Return DTO directly; framework serializes",
    "helidon":   "- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`\n- `@PathParam`, `@QueryParam`\n- Requires JAX-RS `Application` subclass or annotation scan",
    "vertx":     "- No annotations; register routes on `Router`:\n  `router.get(\"/path\").handler(ctx -> ctx.json(obj))`\n- Path params: `router.get(\"/x/:id\")` -> `ctx.pathParam(\"id\")`\n- Response: `ctx.json()`, `ctx.end()`",
}
REST_TARGET = REST_SOURCE

PERSIST_SOURCE = {
    "spring":    "- Spring Data JPA: `interface X extends JpaRepository<E, ID>`\n- `@Transactional` from `org.springframework.transaction`\n- Entity manager auto-configured",
    "jakarta":   "- `@Inject EntityManager em` (persistence.xml unit)\n- `jakarta.transaction.Transactional` on methods\n- JPA queries via `em.createQuery()` or Criteria API",
    "quarkus":   "- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)\n- `jakarta.transaction.Transactional` on service methods\n- Reactive alternative: Hibernate Reactive with `Uni<T>`",
    "micronaut": "- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)\n- Or `@Inject EntityManager em` for direct JPA\n- `@Transactional` from `io.micronaut.transaction.annotation`",
    "helidon":   "- Full JPA: `@Inject EntityManager em`\n- `jakarta.transaction.Transactional`\n- persistence.xml with Hibernate or EclipseLink",
    "vertx":     "- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`\n- Queries: `pool.query(\"SELECT ...\").execute().onSuccess(rows -> ...)`\n- No JPA/EntityManager. No blocking. No `@Transactional`.",
}
PERSIST_TARGET = PERSIST_SOURCE

BOOT_SOURCE = {
    "spring":    "`@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`",
    "jakarta":   "WAR deployed to OpenLiberty; no main class; container-managed startup",
    "quarkus":   "No main class; Quarkus builds one; `quarkus:dev` for hot reload",
    "micronaut": "`@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`",
    "helidon":   "`main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)",
    "vertx":     "`Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`",
}
BOOT_TARGET = BOOT_SOURCE

TARGET_PITFALLS = {
    "micronaut": (
        "- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.\n"
        "- Micronaut uses compile-time DI: adding beans requires recompilation.\n"
        "- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.\n"
        "- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`."
    ),
    "helidon": (
        "- Jandex indexing (`org.jboss.jandex:jandex-maven-plugin`) is strongly recommended; missing index degrades startup.\n"
        "- `helidon-microprofile-cdi` must be explicit; CDI is not auto-added by the parent BOM.\n"
        "- Helidon MP is Weld-based; expect Weld-specific error messages in DI failures."
    ),
    "vertx": (
        "- NEVER block the event loop. `Thread.sleep`, `JDBC`, `EntityManager`, `RestTemplate` — all forbidden on event loop threads.\n"
        "- Wrap unavoidable blocking work in `vertx.executeBlocking(...)`.\n"
        "- Callback chains: use `Future.compose()` / `CompositeFuture` instead of nested `.onSuccess()` handlers.\n"
        "- Config is loaded via `ConfigRetriever`; there is no `application.properties` autoload."
    ),
    "spring": (
        "- Field injection with `@Autowired` works but constructor injection is idiomatic (testability, immutability).\n"
        "- Component scanning defaults to the package of `@SpringBootApplication`; classes elsewhere are ignored."
    ),
    "jakarta": (
        "- OpenLiberty default port is 9080, NOT 8080. Adjust smoke tests if they hardcode 8080.\n"
        "- Missing `<feature>` in `server.xml` causes silent no-op deployment.\n"
        "- Persistence unit name in `persistence.xml` must match `@PersistenceContext(unitName=...)`."
    ),
    "quarkus": (
        "- Native-image constants must be resolvable at build time; runtime-only reflection needs `@RegisterForReflection`.\n"
        "- CDI ambiguity: multiple beans satisfying same interface need `@Named` or producer selection.\n"
        "- Dev services auto-provision databases in dev mode; disable via `quarkus.devservices.enabled=false` for CI."
    ),
}


# ---------- Pair generation ----------

PAIRS = [
    ("spring",    "micronaut"), ("micronaut", "spring"),
    ("jakarta",   "micronaut"), ("micronaut", "jakarta"),
    ("quarkus",   "micronaut"), ("micronaut", "quarkus"),
    ("spring",    "helidon"),   ("helidon",   "spring"),
    ("jakarta",   "helidon"),   ("helidon",   "jakarta"),
    ("quarkus",   "helidon"),   ("helidon",   "quarkus"),
    ("micronaut", "helidon"),   ("helidon",   "micronaut"),
    ("spring",    "vertx"),     ("vertx",     "spring"),
    ("jakarta",   "vertx"),     ("vertx",     "jakarta"),
    ("quarkus",   "vertx"),     ("vertx",     "quarkus"),
    ("micronaut", "vertx"),     ("vertx",     "micronaut"),
    ("helidon",   "vertx"),     ("vertx",     "helidon"),
]

written = 0
for src, tgt in PAIRS:
    bundle_dir = BUNDLES_ROOT / f"{src}-to-{tgt}" / "references"
    if not bundle_dir.exists():
        raise SystemExit(f"Missing bundle dir: {bundle_dir}")

    # dependency-mapping.md driven by TARGET framework
    dep_path = bundle_dir / "dependency-mapping.md"
    dep_path.write_text(TARGET_DEPS[tgt])

    # config-mapping.md driven by TARGET framework
    cfg_path = bundle_dir / "config-mapping.md"
    cfg_path.write_text(TARGET_CONFIG[tgt])

    # code-mapping.md is directional (has source + target sections)
    code_path = bundle_dir / "code-mapping.md"
    code_path.write_text(code_map(src, tgt))

    written += 3

print(f"Wrote {written} reference files ({len(PAIRS)} pairs x 3 files).")
