package vertx.tutorial.interceptor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/").handler(this::showForm);
        router.post("/response").handler(this::response);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Interceptor</title></head>
                <body><h1>Hello</h1>
                <form method="post" action="/response">
                <input type="text" name="name">
                <input type="submit" value="Send">
                </form></body></html>
                """);
    }

    private void response(RoutingContext ctx) {
        String name = ctx.request().getFormAttribute("name");
        // "Interceptor" equivalent: apply lowercase transform (mirrors Spring @Lowercase converter)
        if (name == null) name = "";
        else name = name.toLowerCase();
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Response</title></head>
                <body><h1>Hello, %s</h1></body></html>
                """.formatted(name));
    }
}
