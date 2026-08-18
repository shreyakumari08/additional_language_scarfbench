# Code Mapping — Spring to Vert.x

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation

Target (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories


## Web / REST Endpoints

Source (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`

Target (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`


## Persistence and Transactions

Source (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured

Target (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.


## Bootstrapping / Entry Point

- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`
- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`


## Common Pitfalls (Vert.x)
- NEVER block the event loop. `Thread.sleep`, `JDBC`, `EntityManager`, `RestTemplate` — all forbidden on event loop threads.
- Wrap unavoidable blocking work in `vertx.executeBlocking(...)`.
- Callback chains: use `Future.compose()` / `CompositeFuture` instead of nested `.onSuccess()` handlers.
- Config is loaded via `ConfigRetriever`; there is no `application.properties` autoload.
