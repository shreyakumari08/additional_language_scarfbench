package micronaut.tutorial.billpayment.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.tutorial.billpayment.payment.PaymentService;
import micronaut.tutorial.billpayment.payment.PaymentType;

import java.math.BigDecimal;

@Controller
public class PaymentController {
    @Inject PaymentService paymentService;

    @Get(uri = "/", produces = MediaType.TEXT_HTML)
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

    @Post(uri = "/pay", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> pay(@QueryValue(defaultValue = "1") int paymentOption,
                                     @QueryValue(defaultValue = "0") BigDecimal value) {
        PaymentType type = paymentOption == 2 ? PaymentType.CREDIT : PaymentType.DEBIT;
        String result = paymentService.processPayment(type, value);
        return HttpResponse.ok("""
                <!doctype html><html lang="en"><head><title>Payment Result</title></head>
                <body><h1>Payment %s</h1><p>Type: %s, Value: %s</p></body></html>
                """.formatted(result, type.name(), value));
    }
}
