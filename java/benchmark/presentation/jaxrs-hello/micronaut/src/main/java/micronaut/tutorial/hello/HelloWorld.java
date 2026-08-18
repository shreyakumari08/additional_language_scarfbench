package micronaut.tutorial.hello;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;

@Controller("/helloworld")
public class HelloWorld {

    @Get
    @Produces(MediaType.TEXT_HTML)
    public String getHtml() {
        return "<html lang=\"en\"><body><h1>Hello, World!!</h1></body></html>";
    }

    @Put
    @Consumes(MediaType.TEXT_HTML)
    public HttpResponse<Void> putHtml(@Body String content) {
        return HttpResponse.noContent();
    }
}
