package micronaut.tutorial.web.servlet;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

@Controller
public class Greeting {

    @Get("/greeting")
    public String greet(@QueryValue String name) {
        return "Hello, " + name + "!";
    }
}
