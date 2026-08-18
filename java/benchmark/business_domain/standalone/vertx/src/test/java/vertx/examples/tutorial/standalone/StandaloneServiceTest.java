package vertx.examples.tutorial.standalone;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StandaloneServiceTest {
    @Test void returnsGreetings() {
        assertEquals("Greetings!", new StandaloneService().returnMessage());
    }
}
