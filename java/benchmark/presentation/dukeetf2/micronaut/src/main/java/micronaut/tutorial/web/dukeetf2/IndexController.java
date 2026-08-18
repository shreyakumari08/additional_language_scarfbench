package micronaut.tutorial.web.dukeetf2;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller
public class IndexController {
    @Inject PriceVolumeBean bean;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String index() {
        return """
                <!doctype html><html lang="en"><head><title>Duke ETF (WebSocket)</title></head>
                <body><h1>Duke ETF WebSocket Stream</h1>
                <p>Current tick: %s</p>
                <p>WebSocket endpoint: <code>ws://localhost:8080/dukeetf</code></p>
                </body></html>
                """.formatted(bean.snapshot());
    }
}
