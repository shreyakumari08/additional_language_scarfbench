package micronaut.examples.tutorial.standalone;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import micronaut.examples.tutorial.standalone.service.StandaloneService;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class StandaloneApplicationTest {

    private static final Logger logger = Logger.getLogger("standalone.service");

    @Inject
    StandaloneService standaloneService;

    @Test
    void contextLoads() {}

    @Test
    void testReturnMessage() {
        logger.info("Testing standalone.service.StandaloneService.returnMessage()");
        String expResult = "Greetings!";
        String result = standaloneService.returnMessage();
        assertEquals(expResult, result);
    }

}
