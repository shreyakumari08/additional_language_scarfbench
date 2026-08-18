package helidon.tutorial.rsvp.resource;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/")
public class RootResource {
    @GET @Produces(MediaType.TEXT_HTML)
    public String index() { return "<html><body><h1>RSVP</h1></body></html>"; }
}
