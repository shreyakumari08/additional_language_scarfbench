package vertx.tutorial.addressbook;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MainVerticle extends AbstractVerticle {
    // Vert.x has no JPA; in-memory store preserves REST contract (documented simplification)
    private final ConcurrentHashMap<Long, JsonObject> contacts = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/contacts").handler(this::list);
        router.get("/contacts/count").handler(this::count);
        router.get("/contacts/:id").handler(this::find);
        router.post("/contacts").handler(this::create);
        router.put("/contacts/:id").handler(this::update);
        router.delete("/contacts/:id").handler(this::delete);

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void list(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "application/json")
           .end(new io.vertx.core.json.JsonArray(new java.util.ArrayList<>(contacts.values())).encode());
    }

    private void find(RoutingContext ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        JsonObject c = contacts.get(id);
        if (c == null) { ctx.response().setStatusCode(404).end(); return; }
        ctx.response().putHeader("content-type", "application/json").end(c.encode());
    }

    private void create(RoutingContext ctx) {
        try {
            JsonObject body = ctx.body().asJsonObject();
            long id = seq.getAndIncrement();
            body.put("id", id);
            contacts.put(id, body);
            ctx.response().setStatusCode(201).putHeader("content-type", "application/json").end(body.encode());
        } catch (Exception e) { ctx.response().setStatusCode(500).end(e.getMessage()); }
    }

    private void update(RoutingContext ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        if (!contacts.containsKey(id)) { ctx.response().setStatusCode(404).end(); return; }
        JsonObject body = ctx.body().asJsonObject();
        body.put("id", id);
        contacts.put(id, body);
        ctx.response().putHeader("content-type", "application/json").end(body.encode());
    }

    private void delete(RoutingContext ctx) {
        Long id = Long.valueOf(ctx.pathParam("id"));
        if (contacts.remove(id) == null) { ctx.response().setStatusCode(404).end(); return; }
        ctx.response().setStatusCode(204).end();
    }

    private void count(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/plain").end(String.valueOf(contacts.size()));
    }
}
