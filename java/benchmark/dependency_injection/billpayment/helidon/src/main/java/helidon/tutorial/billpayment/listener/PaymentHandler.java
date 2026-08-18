package helidon.tutorial.billpayment.listener;

import helidon.tutorial.billpayment.event.PaymentEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PaymentHandler {
    private static final Logger logger = Logger.getLogger(PaymentHandler.class.getCanonicalName());

    public void handlePaymentEvent(@Observes PaymentEvent event) {
        if ("Credit".equals(event.getPaymentType())) {
            logger.log(Level.INFO, "PaymentHandler - Credit Handler: {0}", event);
        } else if ("Debit".equals(event.getPaymentType())) {
            logger.log(Level.INFO, "PaymentHandler - Debit Handler: {0}", event);
        }
    }
}
