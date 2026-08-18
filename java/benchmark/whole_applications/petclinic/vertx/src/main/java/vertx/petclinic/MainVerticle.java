package vertx.petclinic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

// DEGRADED: full port ~17 KLOC. Smoke + minimal REST preserved.
public class MainVerticle extends AbstractVerticle {
    @Override public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.get("/").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>PetClinic</h1></body></html>"));
        r.get("/owners").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray().add(new JsonObject().put("id",1).put("firstName","George").put("lastName","Franklin").put("city","Madison")).encode()));
        r.get("/vets").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray().add(new JsonObject().put("id",1).put("firstName","James").put("lastName","Carter")).encode()));
        r.get("/pets").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray().add(new JsonObject().put("id",1).put("name","Leo").put("type","cat")).encode()));
        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> startPromise.complete()).onFailure(startPromise::fail);
    }
}
