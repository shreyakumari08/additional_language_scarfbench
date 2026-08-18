package vertx.examples.tutorial.cart;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.List;

// DEGRADED: original was multi-module (cart-ejb + cart-web). Vert.x is monolithic verticle by design.
// DEGRADED: original had session-scope; Vert.x has no CDI/session — using single in-memory cart.
public class MainVerticle extends AbstractVerticle {
    private String customerId;
    private String customerName;
    private final List<String> contents = new ArrayList<>();

    @Override
    public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.route().handler(BodyHandler.create());

        r.get("/cart").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Cart Service</h1></body></html>"));

        r.post("/cart/api/initialize").handler(this::init);
        r.post("/cart/api/books/:title").handler(this::add);
        r.delete("/cart/api/books/:title").handler(this::remove);
        r.get("/cart/api").handler(this::contents);

        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> { System.out.println("Cart on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void init(RoutingContext ctx) {
        try {
            JsonObject body = ctx.body().asJsonObject();
            String person = body.getString("customerName");
            if (person == null) { ctx.response().setStatusCode(400).end("{\"error\":\"Null person not allowed.\"}"); return; }
            String id = body.getString("customerId", "");
            if (!id.isEmpty()) {
                try { Integer.parseInt(id); } catch (NumberFormatException e) { ctx.response().setStatusCode(400).end("{\"error\":\"Invalid id: " + id + "\"}"); return; }
                customerId = id;
            } else customerId = "0";
            customerName = person; contents.clear();
            ctx.response().putHeader("content-type","application/json")
               .end(new JsonObject().put("message","Cart initialized").put("customerName", person).encode());
        } catch (Exception e) { ctx.response().setStatusCode(400).end("{\"error\":\"" + e.getMessage() + "\"}"); }
    }

    private void add(RoutingContext ctx) {
        String title = ctx.pathParam("title");
        contents.add(title);
        ctx.response().putHeader("content-type","application/json")
           .end(new JsonObject().put("title", title).put("cartSize", contents.size()).encode());
    }

    private void remove(RoutingContext ctx) {
        String title = ctx.pathParam("title");
        if (!contents.remove(title)) { ctx.response().setStatusCode(400).end("{\"error\":\"" + title + " not in cart\"}"); return; }
        ctx.response().putHeader("content-type","application/json")
           .end(new JsonObject().put("removed", title).encode());
    }

    private void contents(RoutingContext ctx) {
        ctx.response().putHeader("content-type","application/json")
           .end(new JsonArray(contents).encode());
    }
}
