package micronaut.tutorial.taskcreator;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import java.util.List;

@Controller
public class RootController {
    @Inject TaskService service;

    @Get(produces = MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Task Creator</h1><p>Tasks executed: " + service.all().size() + "</p></body></html>";
    }

    @Get(uri = "/tasks", produces = MediaType.APPLICATION_JSON)
    public List<Task> tasks() { return service.all(); }
}
