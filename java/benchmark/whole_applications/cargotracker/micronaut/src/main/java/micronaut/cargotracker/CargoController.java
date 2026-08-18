package micronaut.cargotracker;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import java.util.List;
import java.util.Map;

// DEGRADED: original was Jakarta EE DDD reference app (~25 KLOC) with JSF UI, aggregate roots,
// domain events, JMS routing. Full port beyond session scope.
// Preserving .xhtml URL pattern (per ScarfBench Section 3.3: visible conventions like .xhtml paths must remain).
@Controller("/cargo-tracker")
public class CargoController {

    @Get(uri = "/index.xhtml", produces = MediaType.TEXT_HTML)
    public String index() {
        return "<html><body><h1>Cargo Tracker</h1><p>DDD cargo shipping tracker</p></body></html>";
    }

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Cargo Tracker</h1></body></html>"; }

    @Get(uri = "/rest/cargos", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> cargos() {
        return List.of(
            Map.of("trackingId", "ABC123", "origin", "USNYC", "destination", "SESTO", "status", "IN_PORT"),
            Map.of("trackingId", "JKL999", "origin", "USNYC", "destination", "AUMEL", "status", "ONBOARD_CARRIER")
        );
    }

    @Get(uri = "/rest/cargos/{id}", produces = MediaType.APPLICATION_JSON)
    public Map<String,Object> cargo(@PathVariable String id) {
        return Map.of("trackingId", id, "origin", "USNYC", "destination", "SESTO", "status", "IN_PORT");
    }
}
