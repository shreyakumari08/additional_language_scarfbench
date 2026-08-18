# Code Mapping — Micronaut to Helidon MP

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation

Target (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`


## Web / REST Endpoints

Source (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes

Target (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan


## Persistence and Transactions

Source (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`

Target (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink


## Bootstrapping / Entry Point

- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`
- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)


## Common Pitfalls (Helidon MP)
- Jandex indexing (`org.jboss.jandex:jandex-maven-plugin`) is strongly recommended; missing index degrades startup.
- `helidon-microprofile-cdi` must be explicit; CDI is not auto-added by the parent BOM.
- Helidon MP is Weld-based; expect Weld-specific error messages in DI failures.
