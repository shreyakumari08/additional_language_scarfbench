package micronaut.examples.tutorial.standalone.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import micronaut.examples.tutorial.standalone.service.StandaloneService;

import java.util.Map;

@Controller
public class GreetController {

    @Inject
    StandaloneService standaloneService;

    @Get("/greet")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Map<String, String>> greet() {
        return HttpResponse.ok(Map.of("message", standaloneService.returnMessage()));
    }

}
