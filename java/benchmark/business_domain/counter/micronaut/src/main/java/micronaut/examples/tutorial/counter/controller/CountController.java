package micronaut.examples.tutorial.counter.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import micronaut.examples.tutorial.counter.service.CounterService;

@Controller
public class CountController {

    @Inject CounterService counterService;

    @Get("/")
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
