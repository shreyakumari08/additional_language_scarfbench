package helidon.tutorial.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/") @ApplicationScoped
public class MailerResource {
    @Inject MailerService mailer;

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Async Mailer</h1><p>Sent: " + mailer.getSentCount() + "</p></body></html>";
    }

    @POST @Path("/send") @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.TEXT_PLAIN)
    public String send(@FormParam("to") String to, @FormParam("subject") String subject, @FormParam("body") String body) {
        if (to == null) to = "test@example.com";
        if (subject == null) subject = "Test";
        if (body == null) body = "Body";
        mailer.sendMail(to, subject, body);
        return "queued";
    }

    @GET @Path("/sent") @Produces(MediaType.TEXT_PLAIN)
    public String sentCount() { return String.valueOf(mailer.getSentCount()); }
}
