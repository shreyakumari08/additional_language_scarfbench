package micronaut.tutorial.taskcreator;

import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.context.annotation.Context;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
@Context
public class TaskService {
    // JSR-236 managed executors: immediate + delayed + periodic. Micronaut TaskScheduler supports all three.
    @Inject TaskScheduler scheduler;
    private final AtomicLong seq = new AtomicLong(1);
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        // immediate
        scheduler.schedule(Duration.ZERO, () -> submit("immediate"));
        // delayed 3s
        scheduler.schedule(Duration.ofSeconds(3), () -> submit("delayed"));
        // periodic every 5s
        scheduler.scheduleAtFixedRate(Duration.ofSeconds(5), Duration.ofSeconds(5), () -> submit("periodic"));
    }

    public Task submit(String type) {
        Task t = new Task(seq.getAndIncrement(), type, "done");
        tasks.add(t);
        return t;
    }

    public List<Task> all() { return List.copyOf(tasks); }
}
