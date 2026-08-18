package micronaut.tutorial.mood.web;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;

@Controller
public class MoodController {

    @Get(uri = "/report", produces = MediaType.TEXT_HTML)
    public String getReport(HttpRequest<?> request,
                            @QueryValue(defaultValue = "") String name) {
        String mood = request.getAttribute("mood").map(Object::toString).orElse("");
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

    @Post(uri = "/report", produces = MediaType.TEXT_HTML)
    public String postReport(HttpRequest<?> request,
                             @QueryValue(defaultValue = "") String name) {
        return getReport(request, name);
    }
}
