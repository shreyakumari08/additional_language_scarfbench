# Code Mapping — Quarkus to Helidon MP

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Quarkus):
- CDI: `@ApplicationScoped`, `@RequestScoped`
- `@Inject`
- Quarkus adds `@Startup`, `@Scheduled`

Target (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`


## Web / REST Endpoints

Source (Quarkus):
- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)
- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)
- Return DTO or `Response`

Target (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan


## Persistence and Transactions

Source (Quarkus):
- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)
- `jakarta.transaction.Transactional` on service methods
- Reactive alternative: Hibernate Reactive with `Uni<T>`

Target (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink


## Bootstrapping / Entry Point

- Quarkus: No main class; Quarkus builds one; `quarkus:dev` for hot reload
- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)


## Common Pitfalls (Helidon MP)
- Jandex indexing (`org.jboss.jandex:jandex-maven-plugin`) is strongly recommended; missing index degrades startup.
- `helidon-microprofile-cdi` must be explicit; CDI is not auto-added by the parent BOM.
- Helidon MP is Weld-based; expect Weld-specific error messages in DI failures.
