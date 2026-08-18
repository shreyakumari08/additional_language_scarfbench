package helidon.tutorial.taskcreator;

import io.helidon.microprofile.scheduling.FixedRate;
import io.helidon.microprofile.scheduling.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TaskService {
    // JSR-236 managed executors preserved via ScheduledExecutorService for immediate/delayed;
    // periodic uses Helidon @FixedRate scheduler
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final AtomicLong seq = new AtomicLong(1);
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        executor.schedule(() -> submit("immediate"), 0, TimeUnit.SECONDS);
        executor.schedule(() -> submit("delayed"), 3, TimeUnit.SECONDS);
    }

    @FixedRate(value = 5, timeUnit = TimeUnit.SECONDS)
    public void periodic() { submit("periodic"); }

    public Task submit(String type) {
        Task t = new Task(seq.getAndIncrement(), type, "done");
        tasks.add(t);
        return t;
    }

    public List<Task> all() { return List.copyOf(tasks); }
}
