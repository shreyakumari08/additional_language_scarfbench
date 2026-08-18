package micronaut.tutorial.producermethods.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.tutorial.producermethods.service.Coder;
import micronaut.tutorial.producermethods.service.CoderFactory;

@Controller
public class CoderController {

    @Inject CoderFactory coderFactory;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String showForm() { return renderPage("", 0, "", CoderFactory.SHIFT); }

    @Post(uri = "/encode", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> encode(@QueryValue(defaultValue = "") String inputString,
                                        @QueryValue(defaultValue = "0") int transVal,
                                        @QueryValue(defaultValue = "2") int coderType) {
        Coder coder = coderFactory.getCoder(coderType);
        String coded = coder.codeString(inputString, transVal);
        return HttpResponse.ok(renderPage(inputString, transVal, coded, coderType));
    }

    @Post(uri = "/reset", produces = MediaType.TEXT_HTML)
    public HttpResponse<String> reset() { return HttpResponse.ok(renderPage("", 0, "", CoderFactory.SHIFT)); }

    private String renderPage(String input, int tval, String coded, int coderType) {
        return """
                <!doctype html><html lang="en"><head><title>ProducerMethods</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/producermethods/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="number" name="coderType" value="%d">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval, coderType);
    }
}
