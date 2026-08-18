package vertx.tutorial.producermethods;
public class CoderFactory {
    public static final int TEST = 1;
    public static final int SHIFT = 2;
    private final Coder shiftCoder = new CoderImpl();
    private final Coder testCoder = new TestCoderImpl();
    public Coder getCoder(int coderType) {
        switch (coderType) { case TEST: return testCoder; case SHIFT: default: return shiftCoder; }
    }
}
