package vertx.order;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DEGRADED: original had 7-entity JPA graph with composite keys (Part+PartKey, LineItem+LineItemKey).
// Vert.x has no native JPA; using in-memory Maps preserves REST contract but not entity relationships.
public class MainVerticle extends AbstractVerticle {
    private final Map<Integer, JsonObject> vendors = new HashMap<>();
    private final Map<String, JsonObject> parts = new HashMap<>();
    private final Map<Integer, JsonObject> orders = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.route().handler(BodyHandler.create());

        r.get("/").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Order Service (in-memory DEGRADED)</h1></body></html>"));

        r.post("/init").handler(ctx -> {
            vendors.put(1, new JsonObject().put("id", 1).put("name", "Acme"));
            parts.put("P001-1", new JsonObject().put("partNumber", "P001").put("revision", 1).put("description", "Widget"));
            orders.put(1, new JsonObject().put("orderId", 1).put("status", "N"));
            ctx.response().putHeader("content-type","application/json")
               .end(new JsonObject().put("vendors", 1).put("parts", 1).put("vendorParts", 1).put("orders", 1).encode());
        });

        r.get("/vendors").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(vendors.values())).encode()));

        r.get("/orders").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(orders.values())).encode()));

        r.get("/parts").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(parts.values())).encode()));

        vertx.createHttpServer().requestHandler(r).listen(8081)
             .onSuccess(s -> { System.out.println("Order on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
