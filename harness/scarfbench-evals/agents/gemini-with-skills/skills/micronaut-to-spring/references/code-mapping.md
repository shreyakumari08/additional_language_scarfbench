# Code Mapping — Micronaut to Spring

Use these mappings as defaults; adapt to project architecture.
Focus on annotation and API changes; preserve business logic unchanged.


## Injection and Scopes

Source (Micronaut):
- `@Singleton`, `@Prototype`, `@RequestScope`
- `@Inject` (constructor preferred)
- `@Named`, `@Qualifier` for disambiguation

Target (Spring):
- `@Service`, `@Component`, `@Repository`, `@Configuration`
- `@Autowired` (field or constructor)
- `@Qualifier` for disambiguation


## Web / REST Endpoints

Source (Micronaut):
- `@Controller` on class
- `@Get`, `@Post`, `@Put`, `@Delete` (Micronaut annotations, NOT JAX-RS)
- `@PathVariable`, `@QueryValue`, `@Body`
- Return DTO directly; framework serializes

Target (Spring):
- `@RestController` on class
- `@RequestMapping`, `@GetMapping`, `@PostMapping`
- `@PathVariable`, `@RequestParam`, `@RequestBody`
- Return type: DTO (auto-serialized) or `ResponseEntity<T>`


## Persistence and Transactions

Source (Micronaut):
- `@Repository interface X extends CrudRepository<E, ID>` (Micronaut Data)
- Or `@Inject EntityManager em` for direct JPA
- `@Transactional` from `io.micronaut.transaction.annotation`

Target (Spring):
- Spring Data JPA: `interface X extends JpaRepository<E, ID>`
- `@Transactional` from `org.springframework.transaction`
- Entity manager auto-configured


## Bootstrapping / Entry Point

- Micronaut: `@Singleton public class App { public static void main(...) { Micronaut.run(App.class, args); } }`
- Spring: `@SpringBootApplication` + `main` calling `SpringApplication.run(App.class, args)`


## Common Pitfalls (Spring)
- Field injection with `@Autowired` works but constructor injection is idiomatic (testability, immutability).
- Component scanning defaults to the package of `@SpringBootApplication`; classes elsewhere are ignored.
