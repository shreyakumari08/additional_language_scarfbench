# Code Mapping — Micronaut to Vert.x

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation

Target (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories


## Web / REST Endpoints

Source (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes

Target (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`


## Persistence and Transactions

Source (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`

Target (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.


## Bootstrapping / Entry Point

- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`
- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`


## Common Pitfalls (Vert.x)
- NEVER block the event loop. `Thread.sleep`, `JDBC`, `EntityManager`, `RestTemplate` — all forbidden on event loop threads.
- Wrap unavoidable blocking work in `vertx.executeBlocking(...)`.
- Callback chains: use `Future.compose()` / `CompositeFuture` instead of nested `.onSuccess()` handlers.
- Config is loaded via `ConfigRetriever`; there is no `application.properties` autoload.
