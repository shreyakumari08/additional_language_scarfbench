package vertx.tutorial.web.dukeetf2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MainVerticle extends AbstractVerticle {
    private final Random random = new Random();
    private volatile double price = 100.0;
    private volatile int volume = 300000;
    private final Set<ServerWebSocket> sockets = ConcurrentHashMap.newKeySet();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/").handler(ctx -> ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Duke ETF (WebSocket)</title></head>
                <body><h1>Duke ETF WebSocket Stream</h1>
                <p>Current tick: %s</p>
                <p>WebSocket endpoint: <code>ws://localhost:8080/dukeetf</code></p>
                </body></html>
                """.formatted(snapshot())));

        vertx.setPeriodic(1000, id -> {
            price += 1.0 * (random.nextInt(100) - 50) / 100.0;
            volume += random.nextInt(5000) - 2500;
            String msg = snapshot();
            for (ServerWebSocket ws : sockets) {
                if (!ws.isClosed()) ws.writeTextMessage(msg);
            }
        });

        vertx.createHttpServer()
             .requestHandler(router)
             .webSocketHandler(ws -> {
                 if ("/dukeetf".equals(ws.path())) {
                     sockets.add(ws);
                     ws.closeHandler(v -> sockets.remove(ws));
                     ws.exceptionHandler(t -> sockets.remove(ws));
                 } else {
                     ws.reject();
                 }
             })
             .listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private String snapshot() { return String.format("%.2f / %d", price, volume); }
}
