# Code Mapping — Jakarta EE to Vert.x

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation

Target (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories


## Web / REST Endpoints

Source (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`

Target (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`


## Persistence and Transactions

Source (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API

Target (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.


## Bootstrapping / Entry Point

- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup
- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`


## Common Pitfalls (Vert.x)
- NEVER block the event loop. `Thread.sleep`, `JDBC`, `EntityManager`, `RestTemplate` — all forbidden on event loop threads.
- Wrap unavoidable blocking work in `vertx.executeBlocking(...)`.
- Callback chains: use `Future.compose()` / `CompositeFuture` instead of nested `.onSuccess()` handlers.
- Config is loaded via `ConfigRetriever`; there is no `application.properties` autoload.
