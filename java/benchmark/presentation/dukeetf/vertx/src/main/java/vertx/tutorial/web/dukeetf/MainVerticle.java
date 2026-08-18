package vertx.tutorial.web.dukeetf;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Random;

public class MainVerticle extends AbstractVerticle {
    private final Random random = new Random();
    private volatile double price = 100.0;
    private volatile int volume = 300000;

    @Override
    public void start(Promise<Void> startPromise) {
        // Tick every 1s (mirrors Spring @Scheduled fixedDelay=1000)
        vertx.setPeriodic(1000, id -> {
            price += 1.0 * (random.nextInt(100) - 50) / 100.0;
            volume += random.nextInt(5000) - 2500;
        });

        Router router = Router.router(vertx);
        router.get("/").handler(this::index);
        router.get("/dukeetf").handler(this::tick);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private String snapshot() { return String.format("%.2f / %d", price, volume); }

    private void index(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Duke ETF</title></head>
                <body><h1>Duke ETF</h1><p>Current tick: %s</p></body></html>
                """.formatted(snapshot()));
    }

    private void tick(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(snapshot());
    }
}
