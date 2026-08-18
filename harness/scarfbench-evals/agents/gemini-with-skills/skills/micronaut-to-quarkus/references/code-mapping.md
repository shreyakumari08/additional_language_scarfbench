# Code Mapping — Micronaut to Quarkus

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation

Target (Quarkus):
- CDI: `@ApplicationScoped`, `@RequestScoped`
- `@Inject`
- Quarkus adds `@Startup`, `@Scheduled`


## Web / REST Endpoints

Source (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes

Target (Quarkus):
- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)
- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)
- Return DTO or `Response`


## Persistence and Transactions

Source (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`

Target (Quarkus):
- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)
- `jakarta.transaction.Transactional` on service methods
- Reactive alternative: Hibernate Reactive with `Uni<T>`


## Bootstrapping / Entry Point

- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`
- Quarkus: No main class; Quarkus builds one; `quarkus:dev` for hot reload


## Common Pitfalls (Quarkus)
- Native-image constants must be resolvable at build time; runtime-only reflection needs `@RegisterForReflection`.
- CDI ambiguity: multiple beans satisfying same interface need `@Named` or producer selection.
- Dev services auto-provision databases in dev mode; disable via `quarkus.devservices.enabled=false` for CI.
