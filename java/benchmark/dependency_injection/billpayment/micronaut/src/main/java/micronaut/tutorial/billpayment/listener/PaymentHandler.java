package micronaut.tutorial.billpayment.listener;

import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import micronaut.tutorial.billpayment.event.PaymentEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class PaymentHandler {
    private static final Logger logger = Logger.getLogger(PaymentHandler.class.getCanonicalName());

    @EventListener
    public void handlePaymentEvent(PaymentEvent event) {
        if ("Credit".equals(event.getPaymentType())) {
            logger.log(Level.INFO, "PaymentHandler - Credit Handler: {0}", event);
        } else if ("Debit".equals(event.getPaymentType())) {
            logger.log(Level.INFO, "PaymentHandler - Debit Handler: {0}", event);
        }
    }
}
