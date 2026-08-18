package vertx.cargotracker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

// DEGRADED: full port ~25 KLOC. .xhtml URL and REST preserved.
public class MainVerticle extends AbstractVerticle {
    @Override public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.get("/cargo-tracker/index.xhtml").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Cargo Tracker</h1></body></html>"));
        r.get("/cargo-tracker").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Cargo Tracker</h1></body></html>"));
        r.get("/cargo-tracker/rest/cargos").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray()
                .add(new JsonObject().put("trackingId","ABC123").put("origin","USNYC").put("destination","SESTO").put("status","IN_PORT"))
                .add(new JsonObject().put("trackingId","JKL999").put("origin","USNYC").put("destination","AUMEL").put("status","ONBOARD_CARRIER")).encode()));
        r.get("/cargo-tracker/rest/cargos/:id").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonObject().put("trackingId", ctx.pathParam("id")).put("origin","USNYC").put("destination","SESTO").put("status","IN_PORT").encode()));
        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> startPromise.complete()).onFailure(startPromise::fail);
    }
}
