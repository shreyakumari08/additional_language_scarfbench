package vertx.tutorial.web.servlet;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        // GET /greeting?name=X -> "Hello, X!" text/plain (matches Spring)
        router.get("/greeting").handler(ctx -> {
            String name = ctx.request().getParam("name");
            if (name == null || name.isBlank()) {
                ctx.response().setStatusCode(400)
                   .putHeader("content-type", "text/plain")
                   .end("Missing required parameter: name");
                return;
            }
            ctx.response().putHeader("content-type", "text/plain").end("Hello, " + name + "!");
        });
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
