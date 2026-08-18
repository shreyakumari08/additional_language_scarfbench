package helidon.petclinic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// DEGRADED: full port ~17 KLOC. Smoke + minimal REST preserved.
@Path("/") @ApplicationScoped
public class PetClinicResource {
    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>PetClinic</h1></body></html>"; }

    @GET @Path("/owners") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> owners() {
        return List.of(Map.of("id", 1, "firstName", "George", "lastName", "Franklin", "city", "Madison"));
    }
    @GET @Path("/vets") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> vets() {
        return List.of(Map.of("id", 1, "firstName", "James", "lastName", "Carter"));
    }
    @GET @Path("/pets") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> pets() {
        return List.of(Map.of("id", 1, "name", "Leo", "type", "cat"));
    }
}
