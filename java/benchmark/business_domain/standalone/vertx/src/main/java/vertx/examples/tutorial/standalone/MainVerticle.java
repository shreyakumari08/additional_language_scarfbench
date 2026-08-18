package vertx.examples.tutorial.standalone;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    private final StandaloneService standaloneService = new StandaloneService();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        // Preserve external contract: GET /standalone/greet -> {"message":"Greetings!"}
        router.get("/standalone/greet").handler(ctx ->
            ctx.response()
               .putHeader("content-type", "application/json")
               .end(new JsonObject().put("message", standaloneService.returnMessage()).encode())
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
