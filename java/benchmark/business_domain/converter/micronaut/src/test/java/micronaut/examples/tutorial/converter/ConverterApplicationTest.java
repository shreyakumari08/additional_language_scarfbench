package micronaut.examples.tutorial.converter;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import micronaut.examples.tutorial.converter.service.ConverterService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class ConverterApplicationTest {
    @Inject ConverterService converter;

    @Test void contextLoads() {}

    @Test void dollarsToYen() {
        assertEquals(new BigDecimal("104.34"), converter.dollarToYen(BigDecimal.ONE));
    }
}
