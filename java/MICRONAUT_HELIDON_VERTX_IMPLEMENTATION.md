# Adding Micronaut, Helidon, and Vert.x Variants to SCARFBENCH

**Author's note:** This document is written after producing **one fully validated Micronaut variant** (`benchmark/business_domain/standalone/micronaut/`) that compiles, runs, and serves the correct endpoint. It codifies the exact pattern to follow for the remaining 101 variants (34 apps × 3 new frameworks − 1 already done).

---

## What Was Actually Built

### The exemplar

`benchmark/business_domain/standalone/micronaut/` — a Micronaut 4.7.4 variant of the `standalone` app.

**Proof of correctness:**
```
mvn clean package -DskipTests   → BUILD SUCCESS, target/standalone.jar (11.7 MB fat jar)
java -jar target/standalone.jar → Startup completed in ~1s, server on :8080
GET /standalone/greet           → HTTP 200, {"message":"Greetings!"}
BASE_URL=http://localhost:8080/standalone/greet ./test.sh   → PASS - got HTTP 200
```

**File parity with Spring variant:**

| File | Spring | Micronaut | Notes |
|---|---|---|---|
| `.dockerignore` | 41 lines | 41 lines (identical) | copied verbatim |
| `Dockerfile` | 34 lines | 34 lines (identical) | copied verbatim; `java -jar target/*.jar` works for both |
| `mvnw`, `mvnw.cmd` | | | copied verbatim |
| `pom.xml` | 65 lines | 122 lines | Micronaut needs explicit shade + annotation-processor config; Spring gets it free from spring-boot-starter-parent |
| `test.sh` | 15 lines | 15 lines (identical) | copied verbatim |
| `src/main/java/**/*.java` | 3 files, ~49 lines | 3 files, ~50 lines | direct 1:1 translation |
| `src/main/resources/application.properties` | 3 lines | 3 lines | equivalent keys |
| `src/main/resources/logback.xml` | (not needed) | 11 lines | Micronaut needs explicit SLF4J impl config |
| `src/test/java/**/*.java` | 29 lines | 31 lines | direct 1:1 translation |

Net delta: +1 file (`logback.xml`), +64 lines total. Micronaut variant is convention-compliant.

---

## The Convention (Extracted from Existing Spring/Jakarta/Quarkus Variants)

Verified against `spring/`, `jakarta/`, `quarkus/` for `standalone`, `address-book`, `petclinic`.

### Required Files (Per Variant)

```
<app>/<framework>/
├── .dockerignore          # (identical across all frameworks per app)
├── Dockerfile             # base image maven:3.9.12-ibm-semeru-21-noble; only CMD line varies
├── mvnw, mvnw.cmd, .mvn/  # Maven wrapper (identical, copy from spring)
├── pom.xml                # framework-specific parent + deps + plugin
├── test.sh                # bash + curl HTTP status check (near-identical, port/path varies)
├── src/main/java/**/*.java
├── src/main/resources/application.{properties|yml}
└── src/test/java/**/*.java
```

### Framework-Specific Additions

- **Jakarta**: `src/main/liberty/config/server.xml` (OpenLiberty features + httpEndpoint on 9080)
- **Quarkus**: Optional `src/main/docker/Dockerfile.jvm|native|legacy-jar|native-micro` (unused by default `Dockerfile`)
- **Micronaut**: `src/main/resources/logback.xml` (required for logging)
- **Helidon MP**: `src/main/resources/META-INF/microprofile-config.properties` + possibly `beans.xml`
- **Vert.x**: no config-file requirement (config loaded via `ConfigRetriever` in code)

### Dockerfile Convention

**All variants use `maven:3.9.12-ibm-semeru-21-noble`.** The only line that varies is the CMD:

| Framework | CMD |
|---|---|
| Spring | `CMD ["sh", "-c", "java -jar target/*.jar"]` |
| Jakarta | `CMD ["mvn", "liberty:run"]` |
| Quarkus | `CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]` |
| **Micronaut** | `CMD ["sh", "-c", "java -jar target/*.jar"]` (same as Spring, works because Micronaut shade jar is at `target/{artifactId}.jar`) |
| **Helidon MP** | `CMD ["sh", "-c", "java -jar target/*.jar"]` (fat jar produced by helidon-maven-plugin) |
| **Vert.x** | `CMD ["sh", "-c", "java -jar target/*.jar"]` (shade jar with Main-Class = Launcher) |

### test.sh Convention

Identical bash script for all variants. Two things change per app:
- **Port**: Spring/Quarkus/Micronaut/Helidon/Vert.x = 8080; Jakarta = 9080
- **URL path**: matches the app's context path

**Known issue:** For simple apps like `standalone`, `test.sh` hits the *context root* (`/standalone`) but no variant has a root handler, so all variants return 404 unless the harness overrides `BASE_URL`. This affects Spring, Jakarta, Quarkus, and my Micronaut equally. It's a pre-existing benchmark quirk, not something to fix per-variant.

---

## Framework-Specific pom.xml Blueprints

### Micronaut 4.7.x

Confirmed working via `benchmark/business_domain/standalone/micronaut/pom.xml`.

```xml
<parent>
    <groupId>io.micronaut.platform</groupId>
    <artifactId>micronaut-parent</artifactId>
    <version>4.7.4</version>
</parent>

<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <exec.mainClass>your.package.YourApplication</exec.mainClass>
    <micronaut.runtime>netty</micronaut.runtime>
</properties>

<dependencies>
    <dependency><groupId>io.micronaut</groupId><artifactId>micronaut-http-server-netty</artifactId></dependency>
    <dependency><groupId>io.micronaut.serde</groupId><artifactId>micronaut-serde-jackson</artifactId></dependency>
    <dependency><groupId>ch.qos.logback</groupId><artifactId>logback-classic</artifactId><scope>runtime</scope></dependency>
    <dependency><groupId>io.micronaut.test</groupId><artifactId>micronaut-test-junit5</artifactId><scope>test</scope></dependency>
    <dependency><groupId>org.junit.jupiter</groupId><artifactId>junit-jupiter-api</artifactId><scope>test</scope></dependency>
    <dependency><groupId>org.junit.jupiter</groupId><artifactId>junit-jupiter-engine</artifactId><scope>test</scope></dependency>
</dependencies>

<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <!-- CRITICAL: annotation processor must be on annotation-processor path,
             otherwise @Controller and @Get are silently ignored (routes 404) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths combine.children="append">
                    <path>
                        <groupId>io.micronaut</groupId>
                        <artifactId>micronaut-inject-java</artifactId>
                        <version>${micronaut.core.version}</version>
                    </path>
                    <path>
                        <groupId>io.micronaut.serde</groupId>
                        <artifactId>micronaut-serde-processor</artifactId>
                        <version>${micronaut.serialization.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
        <plugin>
            <groupId>io.micronaut.maven</groupId>
            <artifactId>micronaut-maven-plugin</artifactId>
        </plugin>
        <!-- CRITICAL: shade with createDependencyReducedPom=false to produce
             a runnable target/{artifactId}.jar matching the Dockerfile's `target/*.jar` -->
        <plugin>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals><goal>shade</goal></goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>your.package.YourApplication</mainClass>
                            </transformer>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                        </transformers>
                        <filters>
                            <filter><artifact>*:*</artifact><excludes>
                                <exclude>META-INF/*.SF</exclude>
                                <exclude>META-INF/*.DSA</exclude>
                                <exclude>META-INF/*.RSA</exclude>
                            </excludes></filter>
                        </filters>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Extension additions per app family:**
- Persistence (`address-book`, `order`, `roster`, `petclinic`): add `io.micronaut.data:micronaut-data-hibernate-jpa`, `io.micronaut.sql:micronaut-jdbc-hikari`, JDBC driver
- WebSocket (`dukeetf2`, `websocketbot`): add `io.micronaut:micronaut-websocket`
- Validation: add `io.micronaut.validation:micronaut-validation`
- View templates (`petclinic`): add `io.micronaut.views:micronaut-views-thymeleaf` (or Handlebars/Freemarker)

### Helidon MP 4.x

Not yet built. Blueprint per official Helidon docs:

```xml
<parent>
    <groupId>io.helidon.applications</groupId>
    <artifactId>helidon-mp</artifactId>
    <version>4.1.4</version>
</parent>

<dependencies>
    <dependency><groupId>io.helidon.microprofile.bundles</groupId><artifactId>helidon-microprofile</artifactId></dependency>
    <!-- persistence: helidon-integrations-cdi-jpa + hibernate + JDBC driver -->
</dependencies>

<build>
    <finalName>${project.artifactId}</finalName>
    <!-- parent already configures maven-dependency-plugin for lib/ copy and
         maven-jar-plugin for Main-Class = io.helidon.microprofile.cdi.Main -->
</build>
```

Dockerfile CMD: `CMD ["sh", "-c", "java -jar target/*.jar"]` (parent produces runnable jar).

### Vert.x 4.5.x

Not yet built. Blueprint per official Vert.x Maven starter:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-stack-depchain</artifactId>
            <version>4.5.11</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency><groupId>io.vertx</groupId><artifactId>vertx-web</artifactId></dependency>
    <dependency><groupId>io.vertx</groupId><artifactId>vertx-config</artifactId></dependency>
    <!-- persistence: vertx-pg-client or vertx-jdbc-client (NO JPA/EntityManager) -->
</dependencies>

<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals><goal>shade</goal></goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <manifestEntries>
                                    <Main-Class>io.vertx.core.Launcher</Main-Class>
                                    <Main-Verticle>your.package.MainVerticle</Main-Verticle>
                                </manifestEntries>
                            </transformer>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Dockerfile CMD: `CMD ["sh", "-c", "java -jar target/*.jar"]`.

---

## Source-Code Translation Rubric (Per Layer)

Verified for `standalone`; extend by the same pattern for other apps.

### DI (JSR-330-ish)

| Framework | Class annotation | Injection |
|---|---|---|
| Spring | `@Service` / `@Component` | `@Autowired` field or constructor |
| Jakarta | `@Stateless` (EJB) or `@ApplicationScoped` (CDI) | `@EJB` / `@Inject` |
| Quarkus | `@ApplicationScoped` | `@Inject` |
| **Micronaut** | `@Singleton` (`jakarta.inject.Singleton`) | `@Inject` (`jakarta.inject.Inject`) |
| **Helidon MP** | `@ApplicationScoped` (full CDI) | `@Inject` |
| **Vert.x** | (verticle constructor arg or Guice) | (verticle constructor arg or Guice) |

### REST endpoints

| Framework | Class | Method |
|---|---|---|
| Spring | `@RestController` | `@GetMapping("/path")` → return DTO or `ResponseEntity` |
| Jakarta / Quarkus / Helidon MP | `@Path("/prefix")` | `@GET @Path("/sub")` → return DTO or `Response` |
| **Micronaut** | `@Controller` | `@Get("/sub")` → return DTO or `HttpResponse<T>` |
| **Vert.x** | (no class annotation) | `router.get("/sub").handler(ctx -> ctx.json(dto))` |

### Application entry / bootstrap

| Framework | Entry |
|---|---|
| Spring | `@SpringBootApplication` + `SpringApplication.run(App.class, args)` |
| Jakarta | (deployed as WAR to OpenLiberty; no main class) |
| Quarkus | (no main class; `quarkus:dev` or `java -jar target/quarkus-app/quarkus-run.jar`) |
| **Micronaut** | `Micronaut.run(App.class, args)` |
| **Helidon MP** | `io.helidon.microprofile.cdi.Main` (via parent config) |
| **Vert.x** | `io.vertx.core.Launcher` with `Main-Verticle` in manifest |

### Config

| Framework | File | Sample keys |
|---|---|---|
| Spring | `application.properties` | `server.port=8080`, `server.servlet.contextPath=/x` |
| Jakarta | `src/main/liberty/config/server.xml` | `<httpEndpoint httpPort="9080">` |
| Quarkus | `application.properties` | `quarkus.http.port=8080`, `quarkus.http.root-path=/x` |
| **Micronaut** | `application.properties` (NOT `.yml` unless you add snakeyaml) | `micronaut.server.port=8080`, `micronaut.server.context-path=/x` |
| **Helidon MP** | `META-INF/microprofile-config.properties` | `server.port=8080` |
| **Vert.x** | `conf/config.json` loaded via `ConfigRetriever` | `{ "http.port": 8080 }` |

---

## Reproducible Workflow (Per App × Per New Framework)

Applied to `standalone` × Micronaut in this session; documented for the other 101.

```bash
APP=standalone            # or address-book, petclinic, ...
CAT=business_domain       # or persistence, whole_applications, ...
FW=micronaut              # or helidon, vertx
cd benchmark/benchmark/$CAT/$APP

# Step 1: scaffold directory + copy shared files
mkdir -p $FW/src/main/java/$FW/examples/tutorial/$APP/{controller,service} \
         $FW/src/main/resources \
         $FW/src/test/java/$FW/examples/tutorial/$APP
cp spring/.dockerignore $FW/
cp spring/mvnw spring/mvnw.cmd $FW/
[ -d spring/.mvn ] && cp -R spring/.mvn $FW/.mvn
chmod +x $FW/mvnw

# Step 2: write test.sh (identical to spring's)
cp spring/test.sh $FW/test.sh

# Step 3: write Dockerfile (identical to spring's — java -jar target/*.jar works for all three new frameworks)
cp spring/Dockerfile $FW/Dockerfile

# Step 4: write pom.xml using the framework blueprint above
# Step 5: write src/main/java/**/*.java translating Spring code (see rubric)
# Step 6: write src/main/resources/application.properties + logback.xml

# Step 7: validate build
cd $FW
mvn clean package -DskipTests -Dmaven.repo.local=.m2repo
# expect: BUILD SUCCESS + target/*.jar of expected size

# Step 8: validate runtime
java -jar target/*.jar &
JAVA_PID=$!
sleep 10
BASE_URL=http://localhost:8080/<app-context>/<endpoint> ./test.sh
# expect: PASS - got HTTP 200
kill $JAVA_PID
```

---

## Honest Scope / Cost Estimate

| Item | Value |
|---|---|
| Apps in benchmark | 34 |
| New frameworks to add | 3 (Micronaut, Helidon MP, Vert.x) |
| Total new variants required | 102 |
| Variants completed this session | 1 (Micronaut standalone) |
| Variants remaining | 101 |
| Average per-variant SME time (simple apps: `standalone`, `helloservice`, `counter`, DI apps) | 2-4 hours |
| Average per-variant SME time (persistence apps: `address-book`, `order`, `roster`) | 4-8 hours |
| Average per-variant SME time (whole-app: `petclinic`, `realworld`) | 1-2 days |
| Average per-variant SME time (worst case: `daytrader`, `cargotracker` in Vert.x — architectural rewrite) | 3-5 days |
| Rough total for 101 remaining variants | **8-16 SME-weeks**, or **~2-4 SMEs × 4-8 weeks** |
| Cost blowup risk on Vert.x tier | High — Vert.x forces event-loop rewrite; JPA apps must become reactive |

**Recommendation:** wave the implementation.

1. **Wave 0 (validation pilot, ~1 week):** finish Micronaut for `standalone` (done), `helloservice`, `counter`, `address-book`. Run one agent through the harness to verify skill bundles work end-to-end.
2. **Wave 1 (~2-3 weeks):** all 29 focused-tier apps in Micronaut + Helidon MP.
3. **Wave 2 (~2-3 weeks):** whole-app tier in Micronaut + Helidon MP.
4. **Wave 3 (~3-6 weeks):** Vert.x variants. Scope-reduce to non-JPA apps first; treat JPA apps as separate architectural-rewrite projects.

---

## Skeptic Disclosures

1. **1 out of 102 variants is done.** This is proof-of-pattern, not full delivery. Anyone claiming otherwise is misrepresenting scope.
2. **The pattern is proven to work at least for `standalone`.** Complex apps (persistence, WebSocket, view templates) will surface additional Micronaut/Helidon/Vert.x-specific quirks not encountered here. Expect debugging.
3. **Java version quirk:** local build used JDK 26; targets 17 via `--release 17`. Docker uses JDK 21 base image. Both work but keep track if backporting for older Java targets.
4. **`test.sh` pre-existing issue:** `/standalone` returns 404 in Spring AND Micronaut (no root handler in either). The harness must override `BASE_URL`. My Micronaut variant preserved this convention rather than "fixing" it, per the user's instruction to match existing implementations exactly.
5. **No harness integration testing was done.** The variant compiles and runs standalone; whether the SCARFBENCH harness (`scarfbench-cli/`, `harbor/`) accepts and evaluates it correctly is unverified in this session.

---

## Files Produced (This Session)

| Path | Purpose | Status |
|---|---|---|
| `benchmark/business_domain/standalone/micronaut/**` (12 files) | Working Micronaut variant of `standalone` | Compiled, runtime-tested, endpoint-verified |
| `benchmark/MICRONAUT_HELIDON_VERTX_IMPLEMENTATION.md` (this file) | Reproducibility guide + honest scope | Reference document |

That's the ground truth. Everything else is a plan.
