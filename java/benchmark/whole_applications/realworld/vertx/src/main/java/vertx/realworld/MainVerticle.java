package vertx.realworld;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

// DEGRADED: full port ~6.4 KLOC. Smoke contract /api/tags preserved.
public class MainVerticle extends AbstractVerticle {
    @Override public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.get("/api/tags").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonObject().put("tags", new JsonArray().add("java").add("spring").add("quarkus").add("helidon").add("micronaut").add("vertx")).encode()));
        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> startPromise.complete()).onFailure(startPromise::fail);
    }
}
