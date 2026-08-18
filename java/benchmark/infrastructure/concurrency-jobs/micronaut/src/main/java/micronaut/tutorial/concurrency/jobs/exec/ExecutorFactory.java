package micronaut.tutorial.concurrency.jobs.exec;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.util.concurrent.*;

@Factory
public class ExecutorFactory {

    @Singleton
    @High
    public ThreadPoolExecutor highExecutor() {
        int cores = Math.max(4, Runtime.getRuntime().availableProcessors());
        return new ThreadPoolExecutor(cores, cores * 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000), namedThreadFactory("high"));
    }

    @Singleton
    @Low
    public ThreadPoolExecutor lowExecutor() {
        int cores = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        return new ThreadPoolExecutor(cores, cores, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2_000), namedThreadFactory("low"));
    }

    private static ThreadFactory namedThreadFactory(String pool) {
        ThreadFactory delegate = Executors.defaultThreadFactory();
        return r -> {
            Thread t = delegate.newThread(r);
            t.setName("jobs-" + pool + "-" + t.getId());
            t.setDaemon(false);
            return t;
        };
    }
}
