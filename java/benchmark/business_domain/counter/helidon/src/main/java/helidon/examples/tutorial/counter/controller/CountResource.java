package helidon.examples.tutorial.counter.controller;

import helidon.examples.tutorial.counter.service.CounterService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class CountResource {

    @Inject CounterService counterService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        int hitCount = counterService.getHits();
        return """
                <!DOCTYPE html>
                <html lang="en">
                  <head><meta charset="UTF-8"><title>Counter - A singleton session bean example.</title></head>
                  <body>
                    <h1>This page has been accessed %d time(s).</h1>
                    <p>Hooray!</p>
                  </body>
                </html>
                """.formatted(hitCount);
    }
}
