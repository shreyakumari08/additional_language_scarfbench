# Code Mapping — Vert.x to Micronaut

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories

Target (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`

Target (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes


## Persistence and Transactions

Source (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.

Target (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`


## Bootstrapping / Entry Point

- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`
- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`


## Common Pitfalls (Micronaut)
- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.
- Micronaut uses compile-time DI: adding beans requires recompilation.
- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.
- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`.
