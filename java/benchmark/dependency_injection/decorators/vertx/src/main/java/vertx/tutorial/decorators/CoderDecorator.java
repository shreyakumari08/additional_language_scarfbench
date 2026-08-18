package vertx.tutorial.decorators;
public class CoderDecorator implements Coder {
    private final Coder delegate;
    public CoderDecorator(Coder delegate) { this.delegate = delegate; }
    @Override
    public String codeString(String s, int tval) {
        return "\"" + s + "\" becomes \"" + delegate.codeString(s, tval) + "\", " + s.length() + " characters in length";
    }
}
