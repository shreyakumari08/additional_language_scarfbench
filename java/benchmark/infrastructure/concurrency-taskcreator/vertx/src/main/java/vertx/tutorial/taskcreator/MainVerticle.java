package vertx.tutorial.taskcreator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class MainVerticle extends AbstractVerticle {
    // Managed executors preserved via Vert.x setTimer (delayed) + setPeriodic (periodic) + immediate runOnContext
    private final CopyOnWriteArrayList<JsonObject> tasks = new CopyOnWriteArrayList<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public void start(Promise<Void> startPromise) {
        // immediate
        vertx.runOnContext(v -> submit("immediate"));
        // delayed 3s
        vertx.setTimer(3000, id -> submit("delayed"));
        // periodic every 5s
        vertx.setPeriodic(5000, id -> submit("periodic"));

        Router router = Router.router(vertx);
        router.get("/").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>Task Creator</h1><p>Tasks executed: " + tasks.size() + "</p></body></html>"));
        router.get("/tasks").handler(ctx -> ctx.response().putHeader("content-type", "application/json")
            .end(new JsonArray(new java.util.ArrayList<>(tasks)).encode()));

        // WebSocket at /info
        vertx.createHttpServer().webSocketHandler(ws -> {
            if ("/info".equals(ws.path())) {
                ws.writeTextMessage("connected");
                ws.textMessageHandler(msg -> ws.writeTextMessage("echo: " + msg));
            } else {
                ws.reject();
            }
        }).requestHandler(router).listen(9080)
          .onSuccess(s -> { System.out.println("Vert.x server started on port " + s.actualPort()); startPromise.complete(); })
          .onFailure(startPromise::fail);
    }

    private void submit(String type) {
        JsonObject t = new JsonObject().put("id", seq.getAndIncrement()).put("name", type).put("status", "done").put("timestamp", System.currentTimeMillis());
        tasks.add(t);
    }
}
