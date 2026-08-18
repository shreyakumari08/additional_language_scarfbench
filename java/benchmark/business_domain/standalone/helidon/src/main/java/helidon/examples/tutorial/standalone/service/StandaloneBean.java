package helidon.examples.tutorial.standalone.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StandaloneBean {

    private static final String message = "Greetings!";

    public String returnMessage() {
        return message;
    }
}
