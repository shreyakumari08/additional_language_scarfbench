package vertx.tutorial.producermethods;
public class TestCoderImpl implements Coder {
    @Override
    public String codeString(String s, int tval) { return "input string is " + s + ", shift value is " + tval; }
}
