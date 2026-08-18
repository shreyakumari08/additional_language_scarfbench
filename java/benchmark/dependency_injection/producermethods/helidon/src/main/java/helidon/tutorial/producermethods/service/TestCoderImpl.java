package helidon.tutorial.producermethods.service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
@ApplicationScoped
@Named("testCoder")
public class TestCoderImpl implements Coder {
    @Override
    public String codeString(String s, int tval) { return "input string is " + s + ", shift value is " + tval; }
}
