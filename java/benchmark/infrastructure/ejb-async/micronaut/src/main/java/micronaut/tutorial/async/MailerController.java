package micronaut.tutorial.async;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

// DEGRADED: original was JSF ManagedBean. Replaced with REST controller.
@Controller
public class MailerController {
    @Inject MailerService mailer;

    @Get(produces = MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Async Mailer</h1><p>Sent: " + mailer.getSentCount() + "</p></body></html>";
    }

    @Post(uri = "/send", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_PLAIN)
    public String send(@Body java.util.Map<String,String> form) {
        String to = form.getOrDefault("to", "test@example.com");
        String subject = form.getOrDefault("subject", "Test");
        String body = form.getOrDefault("body", "Body");
        mailer.sendMail(to, subject, body);
        return "queued";
    }

    @Get(uri = "/sent", produces = MediaType.TEXT_PLAIN)
    public String sentCount() { return String.valueOf(mailer.getSentCount()); }
}
