package vertx.examples.tutorial.helloservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

// DEGRADED: original was SOAP JAX-WS endpoint. Vert.x has no native JAX-WS.
// Business behavior preserved via REST substitute.
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/helloservice").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>Hello Service (REST-substitute for SOAP)</h1></body></html>"));
        router.get("/helloservice/sayHello").handler(ctx -> {
            String name = ctx.request().getParam("name");
            if (name == null) name = "World";
            ctx.response().putHeader("content-type", "text/plain").end("Hello, " + name + ".");
        });
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("HelloService REST on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
