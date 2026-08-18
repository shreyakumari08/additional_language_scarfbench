package helidon.tutorial.web.dukeetf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class DukeETFResource {
    @Inject PriceVolumeBean bean;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return """
                <!doctype html><html lang="en"><head><title>Duke ETF</title></head>
                <body><h1>Duke ETF</h1><p>Current tick: %s</p></body></html>
                """.formatted(bean.snapshot());
    }

    @GET
    @Path("/dukeetf")
    @Produces(MediaType.TEXT_HTML)
    public String tick() { return bean.snapshot(); }
}
