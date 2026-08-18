package micronaut.examples.tutorial.converter.service;

import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class ConverterService {
    private final BigDecimal yenRate = new BigDecimal("104.34");
    private final BigDecimal euroRate = new BigDecimal("0.007");

    public BigDecimal dollarToYen(BigDecimal dollars) {
        return dollars.multiply(yenRate).setScale(2, RoundingMode.UP);
    }

    public BigDecimal yenToEuro(BigDecimal yen) {
        return yen.multiply(euroRate).setScale(2, RoundingMode.UP);
    }
}
