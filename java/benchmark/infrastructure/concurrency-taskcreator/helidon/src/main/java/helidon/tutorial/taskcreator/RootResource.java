package helidon.tutorial.taskcreator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@ApplicationScoped
public class RootResource {
    @Inject TaskService service;

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Task Creator</h1><p>Tasks executed: " + service.all().size() + "</p></body></html>";
    }

    @GET @Path("/tasks") @Produces(MediaType.APPLICATION_JSON)
    public List<Task> tasks() { return service.all(); }
}
