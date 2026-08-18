package helidon.examples.tutorial.standalone.controller;

import helidon.examples.tutorial.standalone.service.StandaloneBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/")
@ApplicationScoped
public class GreetResource {

    @Inject
    StandaloneBean standaloneBean;

    @GET
    @Path("/greet")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> greet() {
        return Map.of("message", standaloneBean.returnMessage());
    }
}
