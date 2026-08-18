package vertx.daytrader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

// DEGRADED: full port ~14 KLOC. Quotes/portfolio/market-summary preserved.
public class MainVerticle extends AbstractVerticle {
    @Override public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.get("/daytrader").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>DayTrader</h1></body></html>"));
        r.get("/daytrader/").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>DayTrader</h1></body></html>"));
        r.get("/daytrader/rest/quotes/:symbol").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonObject().put("symbol", ctx.pathParam("symbol")).put("price",100.0).put("high",105.0).put("low",95.0).put("volume",1000000).encode()));
        r.get("/daytrader/rest/portfolio/:userID").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonObject().put("userID", ctx.pathParam("userID")).put("holdings", new io.vertx.core.json.JsonArray()).put("balance",10000.0).encode()));
        r.get("/daytrader/rest/market-summary").handler(ctx -> ctx.response().putHeader("content-type","application/json")
            .end(new JsonObject().put("tsia",100.0).put("openTsia",99.0).put("totalVolume",5000000).encode()));
        vertx.createHttpServer().requestHandler(r).listen(9080)
             .onSuccess(s -> startPromise.complete()).onFailure(startPromise::fail);
    }
}
