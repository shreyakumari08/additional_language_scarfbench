package helidon.tutorial.concurrency.jobs.exec;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import java.util.concurrent.*;

@ApplicationScoped
public class ExecutorProducer {

    // @Produces without a normal scope defaults to @Dependent (no proxying).
    // Weld cannot proxy ThreadPoolExecutor (final methods), so return ExecutorService interface
    // and use @Dependent to skip proxy generation.
    @Produces @High
    public ExecutorService highExecutor() {
        int cores = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new ThreadPoolExecutor(cores, cores * 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000));
    }

    @Produces @Low
    public ExecutorService lowExecutor() {
        int cores = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        return new ThreadPoolExecutor(cores, cores, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2_000));
    }

    public void disposeHigh(@Disposes @High ExecutorService svc) { svc.shutdown(); }
    public void disposeLow(@Disposes @Low ExecutorService svc)   { svc.shutdown(); }
}
