package micronaut.tutorial.async;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

// DEGRADED: original was EJB @Asynchronous. Micronaut uses @Async annotation on TaskExecutors.
// SMTP delivery mocked (original had async-smtpd module). Async behavior preserved.
@Singleton
public class MailerService {
    private final AtomicInteger sent = new AtomicInteger();

    @Async
    public CompletableFuture<Void> sendMail(String to, String subject, String body) {
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        sent.incrementAndGet();
        System.out.println("Mail sent to " + to + ": " + subject);
        return CompletableFuture.completedFuture(null);
    }

    public int getSentCount() { return sent.get(); }
}
