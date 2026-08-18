package vertx.tutorial.mood;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    private static final String MOOD = "awake";

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        // Filter equivalent: middleware that puts "mood" attribute on every request context
        router.route().handler(ctx -> {
            ctx.put("mood", MOOD);
            ctx.next();
        });

        // static images at /images/*
        router.route("/images/*").handler(StaticHandler.create("webroot"));

        router.get("/report").handler(this::report);
        router.post("/report").handler(this::report);

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void report(RoutingContext ctx) {
        String mood = ctx.get("mood", "");
        String html = """
                <!doctype html>
                <html lang="en">
                  <head><meta charset="utf-8"><title>Servlet MoodServlet</title></head>
                  <body>
                    <h1>Mood report</h1>
                    <p>Duke's mood is: %s</p>
                    <img src="/images/duke.waving.gif" alt="duke waving">
                  </body>
                </html>
                """.formatted(mood);
        ctx.response().putHeader("content-type", "text/html").end(html);
    }
}
