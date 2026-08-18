package micronaut.tutorial.simplegreeting.service;

import jakarta.inject.Singleton;
import micronaut.tutorial.simplegreeting.Informal;

@Singleton
@Informal
public class InformalGreeting extends Greeting {
    @Override
    public String greet(String name) {
        return "Hi, " + name + "!";
    }
}
