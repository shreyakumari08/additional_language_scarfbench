package helidon.tutorial.hello;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("helloworld")
public class HelloWorld {
    @GET
    @Produces("text/html")
    public String getHtml() { return "<html lang=\"en\"><body><h1>Hello, World!!</h1></body></html>"; }

    @PUT
    @Consumes("text/html")
    public void putHtml(String content) {}
}
