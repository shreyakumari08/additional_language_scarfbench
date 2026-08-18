package vertx.tutorial.producermethods;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
    private final CoderFactory factory = new CoderFactory();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/producermethods/").handler(this::showForm);
        router.get("/producermethods").handler(this::showForm);
        router.post("/producermethods/encode").handler(this::encode);
        router.post("/producermethods/reset").handler(ctx -> ctx.response().putHeader("content-type", "text/html").end(renderPage("", 0, "", CoderFactory.SHIFT)));
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(renderPage("", 0, "", CoderFactory.SHIFT));
    }

    private void encode(RoutingContext ctx) {
        String input = ctx.request().getParam("inputString");
        if (input == null) input = "";
        int tval = 0, type = CoderFactory.SHIFT;
        try { if (ctx.request().getParam("transVal") != null) tval = Integer.parseInt(ctx.request().getParam("transVal")); } catch (NumberFormatException ignored) {}
        try { if (ctx.request().getParam("coderType") != null) type = Integer.parseInt(ctx.request().getParam("coderType")); } catch (NumberFormatException ignored) {}
        String coded = factory.getCoder(type).codeString(input, tval);
        ctx.response().putHeader("content-type", "text/html").end(renderPage(input, tval, coded, type));
    }

    private String renderPage(String input, int tval, String coded, int type) {
        return """
                <!doctype html><html lang="en"><head><title>ProducerMethods</title></head>
                <body><h1>Coder</h1><p>Coded: %s</p>
                <form method="post" action="/producermethods/encode">
                <input type="text" name="inputString" value="%s">
                <input type="number" name="transVal" value="%d" min="0" max="26">
                <input type="number" name="coderType" value="%d">
                <input type="submit" value="Encode">
                </form></body></html>
                """.formatted(coded, input, tval, type);
    }
}
