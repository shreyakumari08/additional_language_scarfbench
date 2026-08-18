package helidon.tutorial.customer.resource;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
@Path("/")
public class RootResource {
    @GET @Produces(MediaType.TEXT_HTML)
    public String index() { return "<html><body><h1>Customer REST API</h1></body></html>"; }
}
