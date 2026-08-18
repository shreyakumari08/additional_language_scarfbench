package micronaut.tutorial.encoder.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.tutorial.encoder.service.Coder;

@Controller
public class CoderController {

    @Inject Coder coderService;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", 0, ""); }

    @Post(uri = "/encode", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> encode(@QueryValue(defaultValue = "") String inputString,
                                        @QueryValue(defaultValue = "0") int transVal) {
        String coded = coderService.codeString(inputString, transVal);
        return HttpResponse.ok(renderPage(inputString, transVal, coded));
    }

    @Post(uri = "/reset", produces = MediaType.TEXT_HTML)
    public HttpResponse<String> reset() { return HttpResponse.ok(renderPage("", 0, "")); }

    private String renderPage(String input, int transVal, String coded) {
        return """
                <!doctype html>
                <html lang="en"><head><title>Encoder</title></head>
                <body><h1>Coder</h1>
                <p>Coded: %s</p>
                <form method="post" action="/encoder/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, transVal);
    }
}
