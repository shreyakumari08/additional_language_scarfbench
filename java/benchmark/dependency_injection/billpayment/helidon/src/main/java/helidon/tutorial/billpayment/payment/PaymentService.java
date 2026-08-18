package helidon.tutorial.billpayment.payment;

import helidon.tutorial.billpayment.event.PaymentEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@ApplicationScoped
public class PaymentService {
    @Inject Event<PaymentEvent> eventPublisher;

    public String processPayment(PaymentType paymentType, BigDecimal value) {
        Date datetime = Calendar.getInstance().getTime();
        switch (paymentType) {
            case DEBIT -> eventPublisher.fire(new PaymentEvent("Debit", value, datetime));
            case CREDIT -> eventPublisher.fire(new PaymentEvent("Credit", value, datetime));
            default -> { return "error"; }
        }
        return "success";
    }
}
