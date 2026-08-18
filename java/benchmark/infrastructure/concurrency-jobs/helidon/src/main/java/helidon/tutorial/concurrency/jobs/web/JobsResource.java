package helidon.tutorial.concurrency.jobs.web;

import helidon.tutorial.concurrency.jobs.exec.High;
import helidon.tutorial.concurrency.jobs.exec.Low;
import helidon.tutorial.concurrency.jobs.store.TokenStore;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

@Path("/webapi/JobService")
@RequestScoped
public class JobsResource {
    private static final Logger log = Logger.getLogger(JobsResource.class.getName());

    @Inject @High java.util.concurrent.ExecutorService highExecutor;
    @Inject @Low java.util.concurrent.ExecutorService lowExecutor;
    @Inject TokenStore tokenStore;

    @GET
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken() {
        String token = "123X5-" + UUID.randomUUID();
        tokenStore.put(token);
        return token;
    }

    @POST
    @Path("/process")
    @Produces(MediaType.TEXT_PLAIN)
    public Response process(@HeaderParam("X-REST-API-Key") String token,
                             @QueryParam("jobID") int jobID) {
        try {
            if (token != null && !token.isEmpty() && tokenStore.isValid(token)) {
                log.info("Token accepted. Execution with high priority.");
                highExecutor.execute(new JobTask("HIGH-" + jobID));
            } else {
                lowExecutor.execute(new JobTask("LOW-" + jobID));
            }
        } catch (RejectedExecutionException ree) {
            return Response.status(503).entity("Job " + jobID + " NOT submitted. " + ree.getMessage()).build();
        }
        return Response.ok("Job " + jobID + " successfully submitted.").build();
    }

    static class JobTask implements Runnable {
        private final String jobID;
        JobTask(String id) { this.jobID = id; }
        @Override public void run() {
            try { Thread.sleep(10_000); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
    }
}
