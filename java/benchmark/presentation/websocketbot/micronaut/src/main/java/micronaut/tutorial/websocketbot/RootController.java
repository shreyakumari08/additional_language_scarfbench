package micronaut.tutorial.websocketbot;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
@Controller
public class RootController {
    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>WebSocket Bot</h1><p>Connect to ws://host/websocketbot</p></body></html>"; }
}
