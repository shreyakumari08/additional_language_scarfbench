# Code Mapping — Jakarta EE to Micronaut

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation

Target (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`

Target (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes


## Persistence and Transactions

Source (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API

Target (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`


## Bootstrapping / Entry Point

- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup
- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`


## Common Pitfalls (Micronaut)
- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.
- Micronaut uses compile-time DI: adding beans requires recompilation.
- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.
- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`.
