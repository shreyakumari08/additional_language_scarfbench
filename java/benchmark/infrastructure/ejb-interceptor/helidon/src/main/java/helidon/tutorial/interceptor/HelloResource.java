package helidon.tutorial.interceptor;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return """
                <!doctype html><html lang="en"><head><title>Interceptor</title></head>
                <body><h1>Hello</h1>
                <form method="post" action="/response">
                <input type="text" name="name">
                <input type="submit" value="Send">
                </form></body></html>
                """;
    }

    @POST
    @Path("/response")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String response(@FormParam("name") String name) {
        if (name == null) name = "";
        else name = name.toLowerCase();
        return """
                <!doctype html><html lang="en"><head><title>Response</title></head>
                <body><h1>Hello, %s</h1></body></html>
                """.formatted(name);
    }
}
