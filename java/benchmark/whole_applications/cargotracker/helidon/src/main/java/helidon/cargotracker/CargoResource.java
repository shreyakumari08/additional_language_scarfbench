package helidon.cargotracker;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// DEGRADED: full port ~25 KLOC. .xhtml URL and REST preserved (per ScarfBench §3.3 visible conventions).
@Path("/cargo-tracker") @ApplicationScoped
public class CargoResource {

    @GET @Path("/index.xhtml") @Produces(MediaType.TEXT_HTML)
    public String index() {
        return "<html><body><h1>Cargo Tracker</h1></body></html>";
    }

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Cargo Tracker</h1></body></html>"; }

    @GET @Path("/rest/cargos") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> cargos() {
        return List.of(
            Map.of("trackingId","ABC123","origin","USNYC","destination","SESTO","status","IN_PORT"),
            Map.of("trackingId","JKL999","origin","USNYC","destination","AUMEL","status","ONBOARD_CARRIER")
        );
    }

    @GET @Path("/rest/cargos/{id}") @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> cargo(@PathParam("id") String id) {
        return Map.of("trackingId", id, "origin", "USNYC", "destination", "SESTO", "status", "IN_PORT");
    }
}
