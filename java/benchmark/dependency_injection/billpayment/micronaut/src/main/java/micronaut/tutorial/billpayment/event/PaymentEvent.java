package micronaut.tutorial.billpayment.event;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentEvent {
    private final String paymentType;
    private final BigDecimal value;
    private final Date datetime;
    public PaymentEvent(String paymentType, BigDecimal value, Date datetime) {
        this.paymentType = paymentType; this.value = value; this.datetime = datetime;
    }
    public String getPaymentType() { return paymentType; }
    public BigDecimal getValue() { return value; }
    public Date getDatetime() { return datetime; }
    @Override public String toString() { return "PaymentEvent[" + paymentType + ", " + value + ", " + datetime + "]"; }
}
