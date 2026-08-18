package helidon.examples.tutorial.standalone;

import helidon.examples.tutorial.standalone.service.StandaloneBean;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StandaloneTest {
    @Test void beanReturnsGreeting() {
        StandaloneBean bean = new StandaloneBean();
        assertEquals("Greetings!", bean.returnMessage());
    }
}
