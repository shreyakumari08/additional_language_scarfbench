package micronaut.tutorial.producermethods.service;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
@Singleton
@Named("testCoder")
public class TestCoderImpl implements Coder {
    @Override
    public String codeString(String s, int tval) { return "input string is " + s + ", shift value is " + tval; }
}
