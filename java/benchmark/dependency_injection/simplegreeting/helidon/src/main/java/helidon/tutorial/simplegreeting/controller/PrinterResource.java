package helidon.tutorial.simplegreeting.controller;

import helidon.tutorial.simplegreeting.Informal;
import helidon.tutorial.simplegreeting.service.Greeting;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class PrinterResource {

    @Inject @Informal Greeting greeting;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", ""); }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String create(@FormParam("name") String name) {
        if (name == null) name = "";
        String salutation = greeting.greet(name);
        return renderPage(salutation, name);
    }

    private String renderPage(String salutation, String name) {
        return """
                <!doctype html>
                <html lang="en">
                  <head><meta charset="utf-8"><title>Simple Greeting</title></head>
                  <body>
                    <h1>Simple Greeting</h1>
                    <p>Salutation: %s</p>
                    <form method="post" action="/simplegreeting/create">
                      <input type="text" name="name" value="%s">
                      <input type="submit" value="Greet">
                    </form>
                  </body>
                </html>
                """.formatted(salutation, name);
    }
}
