package helidon.tutorial.web.servlet;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("greeting")
public class Greeting {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response greet(@QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            return Response.status(400).entity("Missing required parameter: name").build();
        }
        return Response.ok("Hello, " + name + "!").build();
    }
}
