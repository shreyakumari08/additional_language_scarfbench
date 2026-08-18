package micronaut.tutorial.billpayment.payment;

import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import micronaut.tutorial.billpayment.event.PaymentEvent;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Singleton
public class PaymentService {
    @Inject ApplicationEventPublisher<PaymentEvent> eventPublisher;

    public String processPayment(PaymentType paymentType, BigDecimal value) {
        Date datetime = Calendar.getInstance().getTime();
        switch (paymentType) {
            case DEBIT -> eventPublisher.publishEvent(new PaymentEvent("Debit", value, datetime));
            case CREDIT -> eventPublisher.publishEvent(new PaymentEvent("Credit", value, datetime));
            default -> { return "error"; }
        }
        return "success";
    }
}
