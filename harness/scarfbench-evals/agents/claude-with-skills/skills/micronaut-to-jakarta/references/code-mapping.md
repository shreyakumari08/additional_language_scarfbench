# Code Mapping — Micronaut to Jakarta EE

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation

Target (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes

Target (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`


## Persistence and Transactions

Source (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`

Target (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API


## Bootstrapping / Entry Point

- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`
- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup


## Common Pitfalls (Jakarta EE)
- OpenLiberty default port is 9080, NOT 8080. Adjust smoke tests if they hardcode 8080.
- Missing `<feature>` in `server.xml` causes silent no-op deployment.
- Persistence unit name in `persistence.xml` must match `@PersistenceContext(unitName=...)`.
