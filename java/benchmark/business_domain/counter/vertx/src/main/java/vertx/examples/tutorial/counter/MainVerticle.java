package vertx.examples.tutorial.counter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    private final CounterService counterService = new CounterService();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/counter/").handler(this::index);
        router.get("/counter").handler(this::index);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void index(io.vertx.ext.web.RoutingContext ctx) {
        int hitCount = counterService.getHits();
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                  <head><meta charset="UTF-8"><title>Counter - A singleton session bean example.</title></head>
                  <body>
                    <h1>This page has been accessed %d time(s).</h1>
                    <p>Hooray!</p>
                  </body>
                </html>
                """.formatted(hitCount);
        ctx.response().putHeader("content-type", "text/html").end(html);
    }
}
