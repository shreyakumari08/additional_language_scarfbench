# Code Mapping — Quarkus to Vert.x

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Quarkus):
- CDI: `@ApplicationScoped`, `@RequestScoped`
- `@Inject`
- Quarkus adds `@Startup`, `@Scheduled`

Target (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories


## Web / REST Endpoints

Source (Quarkus):
- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)
- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)
- Return DTO or `Response`

Target (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`


## Persistence and Transactions

Source (Quarkus):
- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)
- `jakarta.transaction.Transactional` on service methods
- Reactive alternative: Hibernate Reactive with `Uni<T>`

Target (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.


## Bootstrapping / Entry Point

- Quarkus: No main class; Quarkus builds one; `quarkus:dev` for hot reload
- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`


## Common Pitfalls (Vert.x)
- NEVER block the event loop. `Thread.sleep`, `JDBC`, `EntityManager`, `RestTemplate` — all forbidden on event loop threads.
- Wrap unavoidable blocking work in `vertx.executeBlocking(...)`.
- Callback chains: use `Future.compose()` / `CompositeFuture` instead of nested `.onSuccess()` handlers.
- Config is loaded via `ConfigRetriever`; there is no `application.properties` autoload.
