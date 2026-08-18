package micronaut.tutorial.producermethods.service;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
public class CoderFactory {
    public static final int TEST = 1;
    public static final int SHIFT = 2;
    private final Coder shiftCoder;
    private final Coder testCoder;
    @Inject
    public CoderFactory(@Named("shiftCoder") Coder shiftCoder, @Named("testCoder") Coder testCoder) {
        this.shiftCoder = shiftCoder;
        this.testCoder = testCoder;
    }
    public Coder getCoder(int coderType) {
        switch (coderType) { case TEST: return testCoder; case SHIFT: default: return shiftCoder; }
    }
}
