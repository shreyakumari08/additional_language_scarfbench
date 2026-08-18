package micronaut.tutorial.web.dukeetf;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller
public class DukeETFController {
    @Inject PriceVolumeBean bean;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String index() {
        return """
                <!doctype html><html lang="en"><head><title>Duke ETF</title></head>
                <body><h1>Duke ETF</h1><p>Current tick: %s</p></body></html>
                """.formatted(bean.snapshot());
    }

    @Get(uri = "/dukeetf", produces = MediaType.TEXT_HTML)
    public String tick() { return bean.snapshot(); }
}
