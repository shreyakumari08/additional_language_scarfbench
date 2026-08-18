package helidon.tutorial.encoder.controller;

import helidon.tutorial.encoder.service.Coder;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class CoderResource {

    @Inject Coder coder;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", 0, ""); }

    @POST
    @Path("/encode")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String encode(@FormParam("inputString") String input, @FormParam("transVal") int tval) {
        if (input == null) input = "";
        return renderPage(input, tval, coder.codeString(input, tval));
    }

    @POST
    @Path("/reset")
    @Produces(MediaType.TEXT_HTML)
    public String reset() { return renderPage("", 0, ""); }

    private String renderPage(String input, int tval, String coded) {
        return """
                <!doctype html><html lang="en"><head><title>Encoder</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/encoder/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval);
    }
}
