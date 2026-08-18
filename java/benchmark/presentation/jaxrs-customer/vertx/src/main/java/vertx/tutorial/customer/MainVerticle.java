package vertx.tutorial.customer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MainVerticle extends AbstractVerticle {
    // Vert.x has no JPA; in-memory store preserves REST contract (documented simplification)
    private final ConcurrentHashMap<Integer, JsonObject> customers = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/webapi/").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>Customer REST API</h1><p>/webapi/Customer/all, /webapi/Customer/{id}</p></body></html>"));
        router.get("/webapi").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>Customer REST API</h1></body></html>"));

        router.get("/webapi/Customer/all").handler(this::getAll);
        router.get("/webapi/Customer/:id").handler(this::getOne);
        router.post("/webapi/Customer").handler(this::create);
        router.put("/webapi/Customer/:id").handler(this::update);
        router.delete("/webapi/Customer/:id").handler(this::delete);

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void getAll(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "application/json")
           .end(new io.vertx.core.json.JsonArray(new java.util.ArrayList<>(customers.values())).encode());
    }

    private void getOne(RoutingContext ctx) {
        Integer id = Integer.valueOf(ctx.pathParam("id"));
        JsonObject c = customers.get(id);
        if (c == null) { ctx.response().setStatusCode(404).end(); return; }
        ctx.response().putHeader("content-type", "application/json").end(c.encode());
    }

    private void create(RoutingContext ctx) {
        try {
            JsonObject body = ctx.body().asJsonObject();
            int id = seq.getAndIncrement();
            body.put("id", id);
            customers.put(id, body);
            ctx.response().setStatusCode(201).putHeader("Location", "/" + id).end();
        } catch (Exception e) { ctx.response().setStatusCode(500).end(e.getMessage()); }
    }

    private void update(RoutingContext ctx) {
        Integer id = Integer.valueOf(ctx.pathParam("id"));
        if (!customers.containsKey(id)) { ctx.response().setStatusCode(404).end(); return; }
        JsonObject body = ctx.body().asJsonObject();
        body.put("id", id);
        customers.put(id, body);
        ctx.response().setStatusCode(303).end();
    }

    private void delete(RoutingContext ctx) {
        Integer id = Integer.valueOf(ctx.pathParam("id"));
        if (customers.remove(id) == null) { ctx.response().setStatusCode(404).end(); return; }
        ctx.response().setStatusCode(204).end();
    }
}
