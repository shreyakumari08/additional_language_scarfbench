# Code Mapping — Helidon MP to Jakarta EE

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`

Target (Jakarta EE):
- `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan

Target (Jakarta EE):
- `@Path` on class
- `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathParam`, `@QueryParam`, entity body via method parameter
- Return type: DTO or `jakarta.ws.rs.core.Response`


## Persistence and Transactions

Source (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink

Target (Jakarta EE):
- `@Inject EntityManager em` (persistence.xml unit)
- `jakarta.transaction.Transactional` on methods
- JPA queries via `em.createQuery()` or Criteria API


## Bootstrapping / Entry Point

- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)
- Jakarta EE: WAR deployed to OpenLiberty; no main class; container-managed startup


## Common Pitfalls (Jakarta EE)
- OpenLiberty default port is 9080, NOT 8080. Adjust smoke tests if they hardcode 8080.
- Missing `<feature>` in `server.xml` causes silent no-op deployment.
- Persistence unit name in `persistence.xml` must match `@PersistenceContext(unitName=...)`.
