package micronaut.examples.tutorial.helloservice;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;

// DEGRADED: original was SOAP JAX-WS endpoint. Micronaut has no native JAX-WS.
// Business behavior preserved via REST substitute: GET /sayHello?name=X returns "Hello, X."
@Controller("/helloservice")
public class HelloController {

    @Get(produces = MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Hello Service (REST-substitute for SOAP)</h1></body></html>";
    }

    @Get(uri = "/sayHello", produces = MediaType.TEXT_PLAIN)
    public String sayHello(@QueryValue(defaultValue = "World") String name) {
        return "Hello, " + name + ".";
    }
}
