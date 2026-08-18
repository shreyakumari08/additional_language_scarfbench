package vertx.tutorial.encoder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
    // Env-based selection (mirrors Spring @Profile("!alternative") default)
    private final Coder coder = "alternative".equals(System.getenv("APP_PROFILE"))
        ? new TestCoderImpl()
        : new CoderImpl();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/encoder/").handler(this::showForm);
        router.get("/encoder").handler(this::showForm);
        router.post("/encoder/encode").handler(this::encode);
        router.post("/encoder/reset").handler(ctx -> ctx.response().putHeader("content-type", "text/html").end(renderPage("", 0, "")));
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(renderPage("", 0, ""));
    }

    private void encode(RoutingContext ctx) {
        String input = ctx.request().getParam("inputString");
        String tvalStr = ctx.request().getParam("transVal");
        if (input == null) input = "";
        int tval = 0;
        try { if (tvalStr != null) tval = Integer.parseInt(tvalStr); } catch (NumberFormatException ignored) {}
        String coded = coder.codeString(input, tval);
        ctx.response().putHeader("content-type", "text/html").end(renderPage(input, tval, coded));
    }

    private String renderPage(String input, int tval, String coded) {
        return """
                <!doctype html><html lang="en"><head><title>Encoder</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/encoder/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval);
    }
}
