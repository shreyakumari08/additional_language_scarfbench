package vertx.examples.tutorial.standalone;

// Plain POJO. No DI framework in Vert.x; verticle owns/uses it directly.
public class StandaloneService {

    private static final String message = "Greetings!";

    public String returnMessage() {
        return message;
    }
}
