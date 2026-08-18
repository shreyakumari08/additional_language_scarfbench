package vertx.tutorial.concurrency.jobs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

public class MainVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);
    private static final String API_TOKEN_HEADER = "X-REST-API-Key";

    private final Set<String> tokens = ConcurrentHashMap.newKeySet();
    private final ThreadPoolExecutor highExecutor;
    private final ThreadPoolExecutor lowExecutor;

    public MainVerticle() {
        int cores = Math.max(4, Runtime.getRuntime().availableProcessors());
        this.highExecutor = new ThreadPoolExecutor(cores, cores * 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000));
        int lowCores = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        this.lowExecutor = new ThreadPoolExecutor(lowCores, lowCores, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2_000));
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/jobs/webapi/JobService/token").handler(this::token);
        router.post("/jobs/webapi/JobService/process").handler(this::process);
        vertx.createHttpServer().requestHandler(router).listen(9080)
             .onSuccess(s -> { System.out.println("Vert.x HTTP server started on port " + s.actualPort()); startPromise.complete(); })
             .onFailure(startPromise::fail);
    }

    private void token(RoutingContext ctx) {
        String token = "123X5-" + UUID.randomUUID();
        tokens.add(token);
        ctx.response().end(token);
    }

    private void process(RoutingContext ctx) {
        String token = ctx.request().getHeader(API_TOKEN_HEADER);
        String jobIDStr = ctx.request().getParam("jobID");
        int jobID = 0;
        try { if (jobIDStr != null) jobID = Integer.parseInt(jobIDStr); } catch (NumberFormatException ignored) {}
        try {
            if (token != null && tokens.contains(token)) {
                log.info("Token accepted. Execution with high priority.");
                highExecutor.execute(new JobTask("HIGH-" + jobID));
            } else {
                log.info("Invalid or missing token! {}", token);
                lowExecutor.execute(new JobTask("LOW-" + jobID));
            }
        } catch (RejectedExecutionException ree) {
            ctx.response().setStatusCode(503).end("Job " + jobID + " NOT submitted. " + ree.getMessage());
            return;
        }
        ctx.response().end("Job " + jobID + " successfully submitted.");
    }

    static class JobTask implements Runnable {
        private final String jobID;
        JobTask(String id) { this.jobID = id; }
        @Override public void run() {
            try { Thread.sleep(10_000); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
    }
}
