# Code Mapping — Helidon MP to Spring

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Helidon MP):
- Full CDI: `@ApplicationScoped`, `@RequestScoped`, `@Dependent`
- `@Inject` (JSR-330)
- `@Named`

Target (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Helidon MP):
- Full JAX-RS via Jersey: `@Path`, `@GET`, `@POST`
- `@PathParam`, `@QueryParam`
- Requires JAX-RS `Application` subclass or annotation scan

Target (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`


## Persistence and Transactions

Source (Helidon MP):
- Full JPA: `@Inject EntityManager em`
- `jakarta.transaction.Transactional`
- persistence.xml with Hibernate or EclipseLink

Target (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured


## Bootstrapping / Entry Point

- Helidon MP: `main` calling `io.helidon.microprofile.server.Server.create().start()` (or CDI SE auto-boot)
- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`


## Common Pitfalls (Spring)
- Field injection with `@Autowired` works but constructor injection is idiomatic (testability, immutability).
- Component scanning defaults to the package of `@SpringBootApplication`; classes elsewhere are ignored.
