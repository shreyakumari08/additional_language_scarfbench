package vertx.tutorial.guessnumber;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Random;

public class MainVerticle extends AbstractVerticle {
    private static final int MAX = 100;
    private final Random random = new Random();

    private int number;
    private int minimum;
    private int maximum;
    private int remainingGuesses;

    public MainVerticle() { resetState(); }

    private void resetState() {
        this.number = random.nextInt(MAX + 1);
        this.minimum = 0; this.maximum = MAX; this.remainingGuesses = 10;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/guessnumber/").handler(this::showForm);
        router.get("/guessnumber").handler(this::showForm);
        router.post("/guessnumber/guess").handler(this::guess);
        router.post("/guessnumber/reset").handler(this::reset);
        vertx.createHttpServer().requestHandler(router).listen(8080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void showForm(RoutingContext ctx) {
        ctx.response().putHeader("content-type", "text/html").end(renderPage(null));
    }

    private void guess(RoutingContext ctx) {
        String u = ctx.request().getParam("userNumber");
        int userNumber = 0;
        try { if (u != null) userNumber = Integer.parseInt(u); } catch (NumberFormatException ignored) {}
        String hint = null;
        if (userNumber < minimum || userNumber > maximum) {
            hint = "Invalid guess";
        } else {
            if (userNumber > number) maximum = userNumber - 1;
            else if (userNumber < number) minimum = userNumber + 1;
            if (remainingGuesses > 0) remainingGuesses--;
            if (userNumber == number) hint = "Correct!";
        }
        ctx.response().putHeader("content-type", "text/html").end(renderPage(hint));
    }

    private void reset(RoutingContext ctx) {
        resetState();
        ctx.response().putHeader("content-type", "text/html").end(renderPage(null));
    }

    private String renderPage(String hint) {
        return """
                <!doctype html><html lang="en"><head><title>Guess Number</title></head>
                <body><h1>Guess the number between %d and %d</h1>
                <p>Remaining guesses: %d</p>
                %s
                <form method="post" action="/guessnumber/guess">
                <input type="number" name="userNumber" min="%d" max="%d">
                <input type="submit" value="Guess">
                </form></body></html>
                """.formatted(minimum, maximum, remainingGuesses,
                              hint == null ? "" : "<p>" + hint + "</p>",
                              minimum, maximum);
    }
}
