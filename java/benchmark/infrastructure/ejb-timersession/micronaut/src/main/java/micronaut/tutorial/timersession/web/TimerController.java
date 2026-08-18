package micronaut.tutorial.timersession.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import micronaut.tutorial.timersession.ejb.TimerSessionService;

@Controller
public class TimerController {

    @Inject TimerSessionService timerSessionService;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
    public String timerPage() {
        return """
                <!doctype html><html lang="en"><head><title>Timer Session</title></head>
                <body><h1>Timer Session</h1>
                <p>Last programmatic timeout: %s</p>
                <p>Last automatic timeout: %s</p>
                <form method="post" action="/set">
                <input type="submit" value="Set Programmatic Timer">
                </form></body></html>
                """.formatted(timerSessionService.getLastProgrammaticTimeout(),
                              timerSessionService.getLastAutomaticTimeout());
    }

    @Post(uri = "/set", produces = MediaType.TEXT_HTML)
    public HttpResponse<String> setTimer() {
        timerSessionService.setTimer(8000);
        return HttpResponse.ok(timerPage());
    }
}
