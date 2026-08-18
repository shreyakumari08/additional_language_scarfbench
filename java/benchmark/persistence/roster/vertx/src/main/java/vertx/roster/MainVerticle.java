package vertx.roster;

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

// DEGRADED: original was multi-module (roster-common + roster-boot) with 5-entity JPA + Criteria API.
// Vert.x has no JPA; in-memory storage preserves REST contract only.
public class MainVerticle extends AbstractVerticle {
    private final Map<String, JsonObject> leagues = new HashMap<>();
    private final Map<String, JsonObject> teams = new HashMap<>();
    private final Map<String, JsonObject> players = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.route().handler(BodyHandler.create());

        r.get("/roster").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Roster Service (in-memory DEGRADED)</h1></body></html>"));

        r.post("/roster/init").handler(ctx -> {
            leagues.put("L1", new JsonObject().put("id", "L1").put("name", "MLS").put("sport", "soccer"));
            teams.put("T1", new JsonObject().put("id", "T1").put("name", "Red Team").put("city", "SF"));
            players.put("P1", new JsonObject().put("id", "P1").put("name", "Alice").put("position", "Forward"));
            ctx.response().putHeader("content-type","application/json")
               .end(new JsonObject().put("leagues", 1).put("teams", 1).put("players", 1).encode());
        });

        r.get("/roster/leagues").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(leagues.values())).encode()));
        r.get("/roster/teams").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(teams.values())).encode()));
        r.get("/roster/players").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonArray(new ArrayList<>(players.values())).encode()));

        vertx.createHttpServer().requestHandler(r).listen(8080)
             .onSuccess(s -> { System.out.println("Roster on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
