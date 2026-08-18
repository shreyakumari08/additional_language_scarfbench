package micronaut.tutorial.rsvp.resource;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller
public class RootController {
    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String index() { return "<html><body><h1>RSVP</h1><p>/webapi/status/{eventId}, /webapi/{eventId}/{inviteId}</p></body></html>"; }
}
