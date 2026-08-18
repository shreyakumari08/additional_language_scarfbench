package micronaut.tutorial.simplegreeting.service;

import jakarta.inject.Singleton;

@Singleton
public class Greeting {
    public String greet(String name) {
        return "Hello, " + name + ".";
    }
}
