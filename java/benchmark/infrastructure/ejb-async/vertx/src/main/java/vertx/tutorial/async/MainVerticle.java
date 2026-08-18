package vertx.tutorial.async;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.concurrent.atomic.AtomicInteger;

// DEGRADED: original was EJB @Asynchronous + JSF. Vert.x uses executeBlocking for async work.
// SMTP delivery mocked. Multi-module flattened.
public class MainVerticle extends AbstractVerticle {
    private final AtomicInteger sent = new AtomicInteger();

    @Override
    public void start(Promise<Void> startPromise) {
        Router r = Router.router(vertx);
        r.route().handler(BodyHandler.create());

        r.get("/").handler(ctx -> ctx.response().putHeader("content-type","text/html")
            .end("<html><body><h1>Async Mailer</h1><p>Sent: " + sent.get() + "</p></body></html>"));

        r.post("/send").handler(ctx -> {
            String to = ctx.request().getFormAttribute("to");
            String subject = ctx.request().getFormAttribute("subject");
            String body = ctx.request().getFormAttribute("body");
            if (to == null) to = "test@example.com";
            String finalTo = to;
            String finalSubject = subject == null ? "Test" : subject;
            String finalBody = body == null ? "Body" : body;
            vertx.executeBlocking(() -> {
                Thread.sleep(100);
                sent.incrementAndGet();
                System.out.println("Mail sent to " + finalTo + ": " + finalSubject);
                return null;
            });
            ctx.response().putHeader("content-type","text/plain").end("queued");
        });

        r.get("/sent").handler(ctx -> ctx.response().putHeader("content-type","text/plain").end(String.valueOf(sent.get())));

        vertx.createHttpServer().requestHandler(r).listen(9080)
             .onSuccess(s -> { System.out.println("EJB-async on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }
}
