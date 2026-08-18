package micronaut.tutorial.simplegreeting.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import micronaut.tutorial.simplegreeting.Informal;
import micronaut.tutorial.simplegreeting.dto.PrinterForm;
import micronaut.tutorial.simplegreeting.service.Greeting;

@Controller
public class PrinterController {

    private final Greeting greeting;

    public PrinterController(@Informal Greeting greeting) {
        this.greeting = greeting;
    }

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String showForm() {
        return renderPage(new PrinterForm());
    }

    @Post(uri = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> create(@Body PrinterForm printerForm) {
        printerForm.setSalutation(greeting.greet(printerForm.getName()));
        return HttpResponse.ok(renderPage(printerForm));
    }

    private String renderPage(PrinterForm form) {
        String salutation = form.getSalutation() == null ? "" : form.getSalutation();
        String name = form.getName() == null ? "" : form.getName();
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
