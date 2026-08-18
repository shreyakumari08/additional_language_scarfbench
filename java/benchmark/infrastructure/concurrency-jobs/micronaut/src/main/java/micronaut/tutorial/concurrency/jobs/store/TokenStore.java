package micronaut.tutorial.concurrency.jobs.store;

import jakarta.inject.Singleton;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TokenStore {
    private final Set<String> store = ConcurrentHashMap.newKeySet();
    public void put(String key) { store.add(key); }
    public boolean isValid(String key) { return store.contains(key); }
}
