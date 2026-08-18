package helidon.tutorial.timersession;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class TimerResource {

    @Inject TimerSessionService service;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String timerPage() {
        return """
                <!doctype html><html lang="en"><head><title>Timer Session</title></head>
                <body><h1>Timer Session</h1>
                <p>Last programmatic timeout: %s</p>
                <p>Last automatic timeout: %s</p>
                <form method="post" action="/set">
                <input type="submit" value="Set Programmatic Timer">
                </form></body></html>
                """.formatted(service.getLastProgrammaticTimeout(), service.getLastAutomaticTimeout());
    }

    @POST
    @Path("/set")
    @Produces(MediaType.TEXT_HTML)
    public String setTimer() {
        service.setTimer(8000);
        return timerPage();
    }
}
