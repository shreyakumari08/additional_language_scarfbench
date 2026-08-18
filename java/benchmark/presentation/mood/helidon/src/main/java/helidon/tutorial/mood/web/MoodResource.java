package helidon.tutorial.mood.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("report")
public class MoodResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getReport(@Context ContainerRequestContext ctx,
                            @QueryParam("name") String name) {
        Object moodObj = ctx.getProperty("mood");
        String mood = moodObj == null ? "" : moodObj.toString();
        return """
               <!doctype html>
               <html lang="en">
                 <head><meta charset="utf-8"><title>Servlet MoodServlet</title></head>
                 <body>
                   <h1>Mood report</h1>
                   <p>Duke's mood is: %s</p>
                   <img src="/images/duke.waving.gif" alt="duke waving">
                 </body>
               </html>
               """.formatted(mood);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public String postReport(@Context ContainerRequestContext ctx,
                             @QueryParam("name") String name) {
        return getReport(ctx, name);
    }
}
