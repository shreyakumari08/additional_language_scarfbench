package micronaut.examples.tutorial.standalone;

import io.micronaut.runtime.Micronaut;

public class StandaloneApplication {

    public static void main(String[] args) {
        Micronaut.run(StandaloneApplication.class, args);
    }

}
