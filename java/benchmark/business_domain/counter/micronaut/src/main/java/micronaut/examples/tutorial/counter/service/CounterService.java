package micronaut.examples.tutorial.counter.service;
import jakarta.inject.Singleton;
@Singleton
public class CounterService { private int hits = 1; public int getHits() { return hits++; } }
