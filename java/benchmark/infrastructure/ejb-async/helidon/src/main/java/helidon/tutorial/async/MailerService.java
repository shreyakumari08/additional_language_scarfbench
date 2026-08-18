package helidon.tutorial.async;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// DEGRADED: original was EJB @Asynchronous. Helidon MP has no @Asynchronous.
// Using ExecutorService directly. SMTP delivery mocked.
@ApplicationScoped
public class MailerService {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AtomicInteger sent = new AtomicInteger();

    public CompletableFuture<Void> sendMail(String to, String subject, String body) {
        return CompletableFuture.runAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            sent.incrementAndGet();
            System.out.println("Mail sent to " + to + ": " + subject);
        }, executor);
    }

    public int getSentCount() { return sent.get(); }
}
