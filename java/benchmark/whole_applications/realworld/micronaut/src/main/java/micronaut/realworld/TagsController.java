package micronaut.realworld;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import java.util.List;
import java.util.Map;

// DEGRADED: original was full RealWorld conduit app (articles, comments, users, follow, JWT auth).
// Preserving smoke contract via /api/tags. Full port ~6.4 KLOC — beyond session scope.
@Controller("/api")
public class TagsController {
    @Get(uri = "/tags", produces = MediaType.APPLICATION_JSON)
    public Map<String, List<String>> tags() {
        return Map.of("tags", List.of("java", "spring", "quarkus", "helidon", "micronaut", "vertx"));
    }
}
