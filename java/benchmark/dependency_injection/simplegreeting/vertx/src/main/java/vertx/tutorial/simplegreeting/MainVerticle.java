package vertx.tutorial.simplegreeting;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

    // Vert.x has no CDI/qualifier; instantiate the "informal" impl directly to preserve Spring @Informal wiring
    private final Greeting greeting = new InformalGreeting();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/simplegreeting/").handler(this::showForm);
        router.get("/simplegreeting").handler(this::showForm);
        router.post("/simplegreeting/create").handler(this::create);

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(renderPage("", ""));
    }

    private void create(RoutingContext ctx) {
        String name = ctx.request().getFormAttribute("name");
        if (name == null) name = "";
        String salutation = greeting.greet(name);
        ctx.response().putHeader("content-type", "text/html").end(renderPage(salutation, name));
    }

    private String renderPage(String salutation, String name) {
        return """
                <!doctype html>
                <html lang="en">
                  <head><meta charset="utf-8"><title>Simple Greeting</title></head>
                  <body>
                    <h1>Simple Greeting</h1>
                    <p>Salutation: %s</p>
                    <form method="post" action="/simplegreeting/create">
                      <input type="text" name="name" value="%s">
                      <input type="submit" value="Greet">
                    </form>
                  </body>
                </html>
                """.formatted(salutation, name);
    }
}
