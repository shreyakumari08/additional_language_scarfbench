# Code Mapping — Helidon MP to Micronaut

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`

Target (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan

Target (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes


## Persistence and Transactions

Source (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink

Target (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`


## Bootstrapping / Entry Point

- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)
- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`


## Common Pitfalls (Micronaut)
- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.
- Micronaut uses compile-time DI: adding beans requires recompilation.
- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.
- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`.
