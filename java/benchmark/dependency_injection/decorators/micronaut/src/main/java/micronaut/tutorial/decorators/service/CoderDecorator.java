package micronaut.tutorial.decorators.service;

import io.micronaut.context.annotation.Primary;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Primary
public class CoderDecorator implements Coder {
    private final Coder delegate;
    public CoderDecorator(@Named("baseCoder") Coder delegate) { this.delegate = delegate; }
    @Override
    public String codeString(String s, int tval) {
        int len = s.length();
        return "\"" + s + "\" becomes \"" + delegate.codeString(s, tval) + "\", " + len + " characters in length";
    }
}
