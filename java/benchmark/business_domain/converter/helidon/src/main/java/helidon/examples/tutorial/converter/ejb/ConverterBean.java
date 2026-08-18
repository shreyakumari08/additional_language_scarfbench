package helidon.examples.tutorial.converter.ejb;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class ConverterBean {
    private final BigDecimal yenRate = new BigDecimal("104.34");
    private final BigDecimal euroRate = new BigDecimal("0.007");
    public BigDecimal dollarToYen(BigDecimal d) { return d.multiply(yenRate).setScale(2, RoundingMode.UP); }
    public BigDecimal yenToEuro(BigDecimal y) { return y.multiply(euroRate).setScale(2, RoundingMode.UP); }
}
