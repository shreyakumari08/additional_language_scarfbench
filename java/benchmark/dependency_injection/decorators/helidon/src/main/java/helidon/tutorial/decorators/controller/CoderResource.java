package helidon.tutorial.decorators.controller;

import helidon.tutorial.decorators.service.Coder;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class CoderResource {

    @Inject @Named("baseCoder") Coder base;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", 0, ""); }

    @POST
    @Path("/encode")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String encode(@FormParam("inputString") String input,
                          @FormParam("transVal") int tval) {
        if (input == null) input = "";
        String base = this.base.codeString(input, tval);
        String coded = "\"" + input + "\" becomes \"" + base + "\", " + input.length() + " characters in length";
        return renderPage(input, tval, coded);
    }

    @POST
    @Path("/reset")
    @Produces(MediaType.TEXT_HTML)
    public String reset() { return renderPage("", 0, ""); }

    private String renderPage(String input, int tval, String coded) {
        return """
                <!doctype html><html lang="en"><head><title>Decorators</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/decorators/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval);
    }
}
