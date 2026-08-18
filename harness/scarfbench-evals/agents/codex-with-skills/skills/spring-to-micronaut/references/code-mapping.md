# Code Mapping — Spring to Micronaut

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation

Target (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`

Target (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes


## Persistence and Transactions

Source (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured

Target (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`


## Bootstrapping / Entry Point

- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`
- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`


## Common Pitfalls (Micronaut)
- `micronaut-inject-java` annotation processor MUST be on the annotation-processor path or DI silently breaks.
- Micronaut uses compile-time DI: adding beans requires recompilation.
- DTOs used in reflection paths (JSON serialization, `@ConfigurationProperties`) need `@Introspected`.
- Micronaut's `@Transactional` lives in `io.micronaut.transaction.annotation`, NOT `jakarta.transaction`.
