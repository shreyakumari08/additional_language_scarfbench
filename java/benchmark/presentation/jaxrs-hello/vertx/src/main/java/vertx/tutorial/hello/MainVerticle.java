package vertx.tutorial.hello;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        // Preserve external contract from Spring's HelloWorld:
        //   GET /helloworld  ->  200 text/html "<html>...Hello, World!!</h1>...</html>"
        router.get("/helloworld").handler(ctx ->
            ctx.response()
               .putHeader("content-type", "text/html")
               .end("<html lang=\"en\"><body><h1>Hello, World!!</h1></body></html>")
        );

        // PUT /helloworld consumes text/html and returns 204 (no-op like Spring's putHtml)
        router.put("/helloworld").consumes("text/html").handler(ctx ->
            ctx.response().setStatusCode(204).end()
        );

        vertx.createHttpServer()
             .requestHandler(router)
             .listen(8080)
             .onSuccess(server -> {
                 System.out.println("Vert.x HTTP server started on port " + server.actualPort());
                 startPromise.complete();
             })
             .onFailure(startPromise::fail);
    }
}
