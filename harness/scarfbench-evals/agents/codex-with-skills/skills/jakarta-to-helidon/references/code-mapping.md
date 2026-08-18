# Code Mapping — Jakarta EE to Helidon MP

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation

Target (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`


## Web / REST Endpoints

Source (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`

Target (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan


## Persistence and Transactions

Source (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API

Target (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink


## Bootstrapping / Entry Point

- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup
- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)


## Common Pitfalls (Helidon MP)
- Jandex indexing (`org.jboss.jandex:jandex-maven-plugin`) is strongly recommended; missing index degrades startup.
- `helidon-microprofile-cdi` must be explicit; CDI is not auto-added by the parent BOM.
- Helidon MP is Weld-based; expect Weld-specific error messages in DI failures.
