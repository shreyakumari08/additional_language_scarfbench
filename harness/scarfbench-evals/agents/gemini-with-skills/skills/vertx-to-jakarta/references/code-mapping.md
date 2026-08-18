# Code Mapping — Vert.x to Jakarta EE

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories

Target (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`

Target (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`


## Persistence and Transactions

Source (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.

Target (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API


## Bootstrapping / Entry Point

- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`
- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup


## Common Pitfalls (Jakarta EE)
- OpenLiberty default port is 9080, NOT 8080. Adjust smoke tests if they hardcode 8080.
- Missing `<feature>` in `server.xml` causes silent no-op deployment.
- Persistence unit name in `persistence.xml` must match `@PersistenceContext(unitName=...)`.
