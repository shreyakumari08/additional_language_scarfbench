package vertx.tutorial.billpayment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(MainVerticle.class.getName());

    @Override
    public void start(Promise<Void> startPromise) {
        // Vert.x EventBus is the framework's equivalent of Spring's ApplicationEventPublisher.
        EventBus bus = vertx.eventBus();
        bus.consumer("payment.event", msg -> {
            JsonObject event = (JsonObject) msg.body();
            String type = event.getString("paymentType");
            if ("Credit".equals(type)) logger.info("PaymentHandler - Credit Handler: " + event);
            else if ("Debit".equals(type)) logger.info("PaymentHandler - Debit Handler: " + event);
        });

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/").handler(this::index);
        router.post("/pay").handler(this::pay);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void index(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Bill Payment</title></head>
                <body><h1>Bill Payment</h1>
                <form method="post" action="/pay">
                <select name="paymentOption">
                <option value="1">Debit</option>
                <option value="2">Credit</option>
                </select>
                <input type="text" name="value" value="0">
                <input type="submit" value="Pay">
                </form></body></html>
                """);
    }

    private void pay(RoutingContext ctx) {
        int option = 1;
        BigDecimal value = BigDecimal.ZERO;
        try {
            String p = ctx.request().getParam("paymentOption");
            if (p != null) option = Integer.parseInt(p);
            String v = ctx.request().getParam("value");
            if (v != null) value = new BigDecimal(v);
        } catch (NumberFormatException ignored) {}
        String type = option == 2 ? "Credit" : "Debit";
        JsonObject event = new JsonObject().put("paymentType", type).put("value", value.toString()).put("datetime", new Date().toString());
        vertx.eventBus().publish("payment.event", event);
        ctx.response().putHeader("content-type", "text/html").end("""
                <!doctype html><html lang="en"><head><title>Payment Result</title></head>
                <body><h1>Payment success</h1><p>Type: %s, Value: %s</p></body></html>
                """.formatted(type.toUpperCase(), value));
    }
}
