package vertx.coffeeshop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// DEGRADED: full port ~61 KLOC. Menu+orders preserved.
public class MainVerticle extends AbstractVerticle {
    private final ConcurrentHashMap<String, JsonObject> orders = new ConcurrentHashMap<>();

    @Override public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.route().handler(BodyHandler.create());

        r.get("/").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Coffee Shop</h1></body></html>"));

        r.get("/menu").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray()
                .add(new JsonObject().put("id","espresso").put("name","Espresso").put("price",3.5))
                .add(new JsonObject().put("id","latte").put("name","Latte").put("price",4.5))
                .add(new JsonObject().put("id","cappuccino").put("name","Cappuccino").put("price",4.0)).encode()));

        r.post("/orders").handler(ctx -> {
            JsonObject b = ctx.body().asJsonObject();
            String id = UUID.randomUUID().toString();
            JsonObject o = new JsonObject().put("id",id).put("item", b == null ? "" : b.getString("item","")).put("status","queued");
            orders.put(id, o);
            ctx.response().putHeader("content-type","application/json").end(o.encode());
        });

        r.get("/orders").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new java.util.ArrayList<>(orders.values())).encode()));

        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> startPromise.complete()).onFailure(startPromise::fail);
    }
}
