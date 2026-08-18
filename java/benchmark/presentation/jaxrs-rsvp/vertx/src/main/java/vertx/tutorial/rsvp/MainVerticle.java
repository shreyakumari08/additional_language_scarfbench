package vertx.tutorial.rsvp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MainVerticle extends AbstractVerticle {
    // In-memory stores (Vert.x has no JPA — documented simplification)
    private final ConcurrentHashMap<Long, JsonObject> events = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, JsonObject> persons = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, JsonObject> responses = new ConcurrentHashMap<>();
    private final AtomicLong eventSeq = new AtomicLong(1);
    private final AtomicLong personSeq = new AtomicLong(1);
    private final AtomicLong responseSeq = new AtomicLong(1);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/").handler(ctx -> ctx.response().putHeader("content-type", "text/html")
            .end("<html><body><h1>RSVP</h1></body></html>"));
        router.get("/webapi/status").handler(ctx -> ctx.response().putHeader("content-type", "application/json")
            .end(new io.vertx.core.json.JsonArray(new java.util.ArrayList<>(events.values())).encode()));
        router.get("/webapi/status/:eventId").handler(ctx -> {
            Long id = Long.valueOf(ctx.pathParam("eventId"));
            JsonObject e = events.get(id);
            if (e == null) { ctx.response().setStatusCode(404).end(); return; }
            ctx.response().putHeader("content-type", "application/json").end(e.encode());
        });
        router.post("/webapi/events").handler(ctx -> {
            long id = eventSeq.getAndIncrement();
            JsonObject e = new JsonObject().put("id", id).put("name", "Sample Event").put("location", "Main Hall");
            events.put(id, e);
            ctx.response().putHeader("content-type", "application/json").end(e.encode());
        });
        router.post("/webapi/persons").handler(ctx -> {
            long id = personSeq.getAndIncrement();
            JsonObject p = new JsonObject().put("id", id).put("name", "Alice");
            persons.put(id, p);
            ctx.response().putHeader("content-type", "application/json").end(p.encode());
        });
        router.get("/webapi/:eventId/:inviteId").handler(ctx -> {
            String key = ctx.pathParam("eventId") + ":" + ctx.pathParam("inviteId");
            JsonObject r = responses.get(key);
            if (r == null) { ctx.response().setStatusCode(404).end(); return; }
            ctx.response().putHeader("content-type", "application/json").end(r.encode());
        });
        router.post("/webapi/:eventId/:inviteId/:response").handler(ctx -> {
            Long eventId = Long.valueOf(ctx.pathParam("eventId"));
            Long inviteId = Long.valueOf(ctx.pathParam("inviteId"));
            if (!events.containsKey(eventId) || !persons.containsKey(inviteId)) { ctx.response().setStatusCode(404).end(); return; }
            long id = responseSeq.getAndIncrement();
            JsonObject r = new JsonObject().put("id", id).put("eventId", eventId).put("personId", inviteId).put("response", ctx.pathParam("response").toUpperCase());
            responses.put(eventId + ":" + inviteId, r);
            ctx.response().putHeader("content-type", "application/json").end(r.encode());
        });

        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
