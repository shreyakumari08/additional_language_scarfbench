package helidon.tutorial.billpayment.controller;

import helidon.tutorial.billpayment.payment.PaymentService;
import helidon.tutorial.billpayment.payment.PaymentType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;

@Path("/")
@RequestScoped
public class PaymentResource {

    @Inject PaymentService paymentService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return """
                <!doctype html><html lang="en"><head><title>Bill Payment</title></head>
                <body><h1>Bill Payment</h1>
                <form method="post" action="/pay">
                <select name="paymentOption">
                <option value="1">Debit</option>
                <option value="2">Credit</option>
                </select>
                <input type="text" name="value" value="0">
                <input type="submit" value="Pay">
                </form></body></html>
                """;
    }

    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String pay(@FormParam("paymentOption") int paymentOption,
                       @FormParam("value") BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        PaymentType type = paymentOption == 2 ? PaymentType.CREDIT : PaymentType.DEBIT;
        String result = paymentService.processPayment(type, value);
        return """
                <!doctype html><html lang="en"><head><title>Payment Result</title></head>
                <body><h1>Payment %s</h1><p>Type: %s, Value: %s</p></body></html>
                """.formatted(result, type.name(), value);
    }
}
