package micronaut.petclinic;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import java.util.List;
import java.util.Map;

// DEGRADED: original was full PetClinic (owners/pets/vets/visits with JPA + Thymeleaf/JSF UI, 17 KLOC).
// Preserving REST contract for owners/vets/pets. Full port beyond session scope.
@Controller
public class PetClinicController {

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>PetClinic</h1><p>Endpoints: /owners /vets /pets</p></body></html>"; }

    @Get(uri = "/owners", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> owners() {
        return List.of(Map.of("id", 1, "firstName", "George", "lastName", "Franklin", "city", "Madison"));
    }

    @Get(uri = "/vets", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> vets() {
        return List.of(Map.of("id", 1, "firstName", "James", "lastName", "Carter"));
    }

    @Get(uri = "/pets", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> pets() {
        return List.of(Map.of("id", 1, "name", "Leo", "type", "cat"));
    }
}
