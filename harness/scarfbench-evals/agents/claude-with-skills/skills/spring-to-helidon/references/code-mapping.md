# Code Mapping — Spring to Helidon MP

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation

Target (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`


## Web / REST Endpoints

Source (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`

Target (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan


## Persistence and Transactions

Source (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured

Target (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink


## Bootstrapping / Entry Point

- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`
- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)


## Common Pitfalls (Helidon MP)
- Jandex indexing (`org.jboss.jandex:jandex-maven-plugin`) is strongly recommended; missing index degrades startup.
- `helidon-microprofile-cdi` must be explicit; CDI is not auto-added by the parent BOM.
- Helidon MP is Weld-based; expect Weld-specific error messages in DI failures.
