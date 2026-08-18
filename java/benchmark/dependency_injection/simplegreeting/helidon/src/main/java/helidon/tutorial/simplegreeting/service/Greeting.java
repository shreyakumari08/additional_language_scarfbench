package helidon.tutorial.simplegreeting.service;

import jakarta.enterprise.context.Dependent;

@Dependent
public class Greeting {
    public String greet(String name) { return "Hello, " + name + "."; }
}
