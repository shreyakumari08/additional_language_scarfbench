# Code Mapping — Vert.x to Spring

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Vert.x):
- No DI framework by default
- Composition via `AbstractVerticle` subclasses and constructor arguments
- Optional integration: Guice or hand-rolled factories

Target (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Vert.x):
- No annotations; register routes on `Router`:
  `router.get("/path").handler(ctx -> ctx.json(obj))`
- Path params: `router.get("/x/:id")` -> `ctx.pathParam("id")`
- Response: `ctx.json()`, `ctx.end()`

Target (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`


## Persistence and Transactions

Source (Vert.x):
- Reactive SQL client: `Pool pool = PgPool.pool(vertx, options)`
- Queries: `pool.query("SELECT ...").execute().onSuccess(rows -> ...)`
- No JPA/EntityManager. No blocking. No `@Transactional`.

Target (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured


## Bootstrapping / Entry Point

- Vert.x: `Launcher` main class deploying a main verticle via `vertx.deployVerticle(new MainVerticle())`
- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`


## Common Pitfalls (Spring)
- Field injection with `@Autowired` works but constructor injection is idiomatic (testability, immutability).
- Component scanning defaults to the package of `@SpringBootApplication`; classes elsewhere are ignored.
