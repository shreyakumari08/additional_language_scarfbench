package vertx.tutorial.timersession;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

public class MainVerticle extends AbstractVerticle {

    private volatile Date lastProgrammaticTimeout;
    private volatile Date lastAutomaticTimeout;

    @Override
    public void start(Promise<Void> startPromise) {
        // Automatic timer every 60 seconds (mirrors Spring @Scheduled cron "0 */1 * * * *")
        vertx.setPeriodic(60_000, id -> lastAutomaticTimeout = new Date());

        Router router = Router.router(vertx);
        router.get("/").handler(this::page);
        router.post("/set").handler(this::setTimer);
        vertx.createHttpServer().requestHandler(router).listen(9080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void setTimer(RoutingContext ctx) {
        // Programmatic timer: schedule one-shot 8s from now
        vertx.setTimer(8000, id -> lastProgrammaticTimeout = new Date());
        page(ctx);
    }

    private void page(RoutingContext ctx) {
        String prog = lastProgrammaticTimeout != null ? lastProgrammaticTimeout.toString() : "never";
        String auto = lastAutomaticTimeout != null ? lastAutomaticTimeout.toString() : "never";
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Timer Session</title></head>
                <body><h1>Timer Session</h1>
                <p>Last programmatic timeout: %s</p>
                <p>Last automatic timeout: %s</p>
                <form method="post" action="/set">
                <input type="submit" value="Set Programmatic Timer">
                </form></body></html>
                """.formatted(prog, auto));
    }
}
