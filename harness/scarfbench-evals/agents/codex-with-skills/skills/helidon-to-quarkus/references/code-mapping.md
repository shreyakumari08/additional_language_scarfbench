# Code Mapping — Helidon MP to Quarkus

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`

Target (Quarkus):
- CDI: `@ApplicationScoped`, `@RequestScoped`
- `@Inject`
- Quarkus adds `@Startup`, `@Scheduled`


## Web / REST Endpoints

Source (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan

Target (Quarkus):
- `@Path`, `@GET`, `@POST` (JAX-RS, same as Jakarta)
- RESTEasy Reactive: async return types (`Uni<T>`, `Multi<T>`)
- Return DTO or `Response`


## Persistence and Transactions

Source (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink

Target (Quarkus):
- `@Inject EntityManager em` or Panache (`extends PanacheEntity`)
- `jakarta.transaction.Transactional` on service methods
- Reactive alternative: Hibernate Reactive with `Uni<T>`


## Bootstrapping / Entry Point

- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)
- Quarkus: No main class; Quarkus builds one; `quarkus:dev` for hot reload


## Common Pitfalls (Quarkus)
- Native-image constants must be resolvable at build time; runtime-only reflection needs `@RegisterForReflection`.
- CDI ambiguity: multiple beans satisfying same interface need `@Named` or producer selection.
- Dev services auto-provision databases in dev mode; disable via `quarkus.devservices.enabled=false` for CI.
