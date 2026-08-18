package vertx.examples.tutorial.converter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

import java.math.BigDecimal;

public class MainVerticle extends AbstractVerticle {

    private final ConverterService converter = new ConverterService();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        // Preserve Spring behavior: GET /converter/ returns HTML form OR conversion result if ?amount=X
        router.get("/converter/").handler(this::render);
        router.get("/converter").handler(this::render);  // no trailing slash tolerance
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void render(io.vertx.ext.web.RoutingContext ctx) {
        String amount = ctx.request().getParam("amount");
        StringBuilder html = new StringBuilder();
        html.append("<html lang=\"en\"><head><title>Servlet ConverterServlet</title></head>")
            .append("<body><h1>Servlet ConverterServlet at ").append(ctx.request().path()).append("</h1>");
        try {
            if (amount != null && !amount.isEmpty()) {
                BigDecimal d = new BigDecimal(amount);
                BigDecimal yenAmount = converter.dollarToYen(d);
                BigDecimal euroAmount = converter.yenToEuro(yenAmount);
                html.append("<p>").append(amount).append(" dollars are ").append(yenAmount.toPlainString()).append(" yen.</p>");
                html.append("<p>").append(yenAmount.toPlainString()).append(" yen are ").append(euroAmount.toPlainString()).append(" Euro.</p>");
            } else {
                html.append("<p>Enter a dollar amount to convert:</p><form method=\"get\">")
                    .append("<p>$ <input title=\"Amount\" type=\"text\" name=\"amount\" size=\"25\"></p><br/>")
                    .append("<input type=\"submit\" value=\"Submit\"><input type=\"reset\" value=\"Reset\"></form>");
            }
        } finally { html.append("</body></html>"); }
        ctx.response().putHeader("content-type", "text/html").end(html.toString());
    }
}
