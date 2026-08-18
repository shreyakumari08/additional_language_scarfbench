package vertx.tutorial.websocketbot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

import java.util.concurrent.CopyOnWriteArraySet;

public class MainVerticle extends AbstractVerticle {
    private final CopyOnWriteArraySet<ServerWebSocket> sessions = new CopyOnWriteArraySet<>();

    private String respond(String input) {
        if (input == null) return "?";
        String i = input.toLowerCase();
        if (i.contains("hello") || i.contains("hi")) return "Hi there!";
        if (i.contains("bye")) return "Goodbye!";
        if (i.contains("how are you")) return "I am well, thanks!";
        return "I heard: " + input;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>WebSocket Bot</h1><p>Connect to ws://host/websocketbot</p></body></html>"));

        vertx.createHttpServer().webSocketHandler(ws -> {
            if (!"/websocketbot".equals(ws.path())) { ws.reject(); return; }
            sessions.add(ws);
            ws.writeTextMessage("{\"type\":\"info\",\"message\":\"connection opened\"}");
            ws.textMessageHandler(msg -> {
                broadcast(msg);
                if (msg.contains("\"target\":\"Duke\"")) {
                    String content = extract(msg, "\"message\":\"", "\"");
                    String resp = respond(content);
                    broadcast("{\"type\":\"chat\",\"name\":\"Duke\",\"target\":\"user\",\"message\":\"" + resp.replace("\"","\\\"") + "\"}");
                }
            });
            ws.closeHandler(v -> sessions.remove(ws));
        }).requestHandler(router).listen(8080)
          .onSuccess(s -> { System.out.println("Vert.x WS bot on port " + s.actualPort()); startPromise.complete(); })
          .onFailure(startPromise::fail);
    }

    private void broadcast(String msg) {
        for (ServerWebSocket s : sessions) {
            if (!s.isClosed()) s.writeTextMessage(msg);
        }
    }
    private String extract(String s, String start, String end) {
        int a = s.indexOf(start); if (a < 0) return "";
        a += start.length(); int b = s.indexOf(end, a); if (b < 0) return "";
        return s.substring(a, b);
    }
}
