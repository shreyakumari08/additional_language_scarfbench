package micronaut.tutorial.guessnumber.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import jakarta.inject.Singleton;

@Factory
public class GeneratorConfig {
    private static final int MAX = 100;

    @Singleton
    public java.util.Random random() { return new java.util.Random(System.currentTimeMillis()); }

    @Prototype
    @Random
    public Integer next(java.util.Random r) { return r.nextInt(MAX + 1); }

    @Singleton
    @MaxNumber
    public Integer maxNumber() { return MAX; }
}
