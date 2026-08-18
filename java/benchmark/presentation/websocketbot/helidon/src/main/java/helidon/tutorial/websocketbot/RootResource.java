package helidon.tutorial.websocketbot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@Path("/") @ApplicationScoped
public class RootResource {
    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>WebSocket Bot</h1><p>Connect to ws://host/websocketbot</p></body></html>"; }
}
