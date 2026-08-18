package helidon.tutorial.simplegreeting.service;

import helidon.tutorial.simplegreeting.Informal;
import jakarta.enterprise.context.Dependent;

@Dependent
@Informal
public class InformalGreeting extends Greeting {
    @Override public String greet(String name) { return "Hi, " + name + "!"; }
}
