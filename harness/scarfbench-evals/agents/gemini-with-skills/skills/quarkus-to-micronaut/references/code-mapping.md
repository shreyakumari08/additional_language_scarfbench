# Code Mapping — Quarkus to Micronaut

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Quarkus):
- CDI: `@ApplicationScoped`, `@RequestScoped`
- `@Inject`
- Quarkus adds `@Startup`, `@Scheduled`

Target (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Quarkus):
- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)
- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)
- Return DTO or `Response`

Target (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes


## Persistence and Transactions

Source (Quarkus):
- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)
- `jakarta.transaction.Transactional` on service methods
- Reactive alternative: Hibernate Reactive with `Uni<T>`

Target (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`


## Bootstrapping / Entry Point

- Quarkus: No main class; Quarkus builds one; `quarkus:dev` for hot reload
- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`


## Common Pitfalls (Micronaut)
- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.
- Micronaut uses compile-time DI: adding beans requires recompilation.
- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.
- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`.
