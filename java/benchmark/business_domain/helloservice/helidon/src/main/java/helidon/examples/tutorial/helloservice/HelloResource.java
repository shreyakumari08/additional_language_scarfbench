package helidon.examples.tutorial.helloservice;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

// DEGRADED: original was SOAP JAX-WS endpoint. Helidon MP has no native JAX-WS.
// Business behavior preserved via REST substitute.
@Path("/helloservice") @ApplicationScoped
public class HelloResource {

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Hello Service (REST-substitute for SOAP)</h1></body></html>";
    }

    @GET @Path("/sayHello") @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@QueryParam("name") @DefaultValue("World") String name) {
        return "Hello, " + name + ".";
    }
}
