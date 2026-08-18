package micronaut.tutorial.concurrency.jobs.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.tutorial.concurrency.jobs.exec.High;
import micronaut.tutorial.concurrency.jobs.exec.Low;
import micronaut.tutorial.concurrency.jobs.store.TokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Controller("/webapi/JobService")
public class JobsController {
    private static final Logger log = LoggerFactory.getLogger(JobsController.class);
    public static final String API_TOKEN_HEADER = "X-REST-API-Key";

    @Inject @High ThreadPoolExecutor highExecutor;
    @Inject @Low ThreadPoolExecutor lowExecutor;
    @Inject TokenStore tokenStore;

    @Get("/token")
    public HttpResponse<String> getToken() {
        String token = "123X5-" + UUID.randomUUID();
        tokenStore.put(token);
        return HttpResponse.ok(token);
    }

    @Post("/process")
    public HttpResponse<String> process(@Header(name = API_TOKEN_HEADER, defaultValue = "") String token,
                                        @QueryValue("jobID") int jobID) {
        try {
            if (token != null && !token.isEmpty() && tokenStore.isValid(token)) {
                log.info("Token accepted. Execution with high priority.");
                highExecutor.execute(new JobTask("HIGH-" + jobID));
            } else {
                log.info("Invalid or missing token! {}", token);
                lowExecutor.execute(new JobTask("LOW-" + jobID));
            }
        } catch (RejectedExecutionException ree) {
            return HttpResponse.<String>status(io.micronaut.http.HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Job " + jobID + " NOT submitted. " + ree.getMessage());
        }
        return HttpResponse.ok("Job " + jobID + " successfully submitted.");
    }

    static class JobTask implements Runnable {
        private static final Logger LOG = LoggerFactory.getLogger(JobTask.class);
        private final String jobID;

        JobTask(String id) { this.jobID = id; }

        @Override public void run() {
            try { LOG.info("Task started {}", jobID); Thread.sleep(10_000); LOG.info("Task finished {}", jobID); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
    }
}
