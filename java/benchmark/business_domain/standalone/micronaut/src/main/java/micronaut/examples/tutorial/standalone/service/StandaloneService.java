package micronaut.examples.tutorial.standalone.service;

import jakarta.inject.Singleton;

@Singleton
public class StandaloneService {

    private static final String message = "Greetings!";

    public String returnMessage() {
        return message;
    }

}
