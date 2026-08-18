package micronaut.tutorial.interceptor;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

@Controller
public class HelloController {

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
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

    @Post(uri = "/response", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public String response(@Body HelloForm helloForm) {
        String name = helloForm.getName() == null ? "" : helloForm.getName().toLowerCase();
        return """
                <!doctype html><html lang="en"><head><title>Response</title></head>
                <body><h1>Hello, %s</h1></body></html>
                """.formatted(name);
    }
}
