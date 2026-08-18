package helidon.tutorial.web.dukeetf2;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class IndexResource {
    @Inject PriceVolumeBean bean;

    @GET
    @Produces(MediaType.TEXT_HTML)
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
