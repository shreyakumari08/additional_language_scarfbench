package helidon.examples.tutorial.counter.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CounterService {
    private int hits = 1;
    public synchronized int getHits() { return hits++; }
}
