package helidon.tutorial.producermethods.controller;

import helidon.tutorial.producermethods.service.Coder;
import helidon.tutorial.producermethods.service.CoderFactory;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class CoderResource {

    @Inject CoderFactory coderFactory;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", 0, "", CoderFactory.SHIFT); }

    @POST
    @Path("/encode")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String encode(@FormParam("inputString") String input,
                          @FormParam("transVal") int tval,
                          @FormParam("coderType") int type) {
        if (input == null) input = "";
        if (type == 0) type = CoderFactory.SHIFT;
        Coder c = coderFactory.getCoder(type);
        return renderPage(input, tval, c.codeString(input, tval), type);
    }

    @POST
    @Path("/reset")
    @Produces(MediaType.TEXT_HTML)
    public String reset() { return renderPage("", 0, "", CoderFactory.SHIFT); }

    private String renderPage(String input, int tval, String coded, int type) {
        return """
                <!doctype html><html lang="en"><head><title>ProducerMethods</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/producermethods/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="number" name="coderType" value="%d">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval, type);
    }
}
