package micronaut.tutorial.customer.resource;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller
public class RootController {
    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String index() { return "<html><body><h1>Customer REST API</h1><p>/Customer/all, /Customer/{id}</p></body></html>"; }
}
