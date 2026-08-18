package vertx.examples.tutorial.counter;
import java.util.concurrent.atomic.AtomicInteger;
public class CounterService {
    private final AtomicInteger hits = new AtomicInteger(1);
    public int getHits() { return hits.getAndIncrement(); }
}
