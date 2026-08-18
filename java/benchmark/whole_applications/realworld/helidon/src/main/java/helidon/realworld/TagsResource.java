package helidon.realworld;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// DEGRADED: full port ~6.4 KLOC. Smoke contract /api/tags preserved.
@Path("/api") @ApplicationScoped
public class TagsResource {
    @GET @Path("/tags") @Produces(MediaType.APPLICATION_JSON)
    public Map<String,List<String>> tags() {
        return Map.of("tags", List.of("java","spring","quarkus","helidon","micronaut","vertx"));
    }
}
