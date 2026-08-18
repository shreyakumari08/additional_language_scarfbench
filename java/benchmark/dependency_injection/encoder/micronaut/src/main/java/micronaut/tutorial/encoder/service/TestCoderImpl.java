package micronaut.tutorial.encoder.service;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(env = "alternative")
public class TestCoderImpl implements Coder {
    @Override
    public String codeString(String s, int tval) {
        return "input string is " + s + ", shift value is " + tval;
    }
}
