package micronaut.examples.tutorial.converter.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import micronaut.examples.tutorial.converter.service.ConverterService;

import java.math.BigDecimal;

@Controller
public class ConverterController {

    @Inject
    ConverterService converter;

    @Get("/")
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> convert(@QueryValue(value = "amount", defaultValue = "") String amount,
                                         HttpRequest<?> request) {
        StringBuilder html = new StringBuilder();
        html.append("<html lang=\"en\">")
            .append("<head><title>Servlet ConverterServlet</title></head>")
            .append("<body>")
            .append("<h1>Servlet ConverterServlet at ").append(request.getPath()).append("</h1>");
        try {
            if (amount != null && !amount.isEmpty()) {
                BigDecimal d = new BigDecimal(amount);
                BigDecimal yenAmount = converter.dollarToYen(d);
                BigDecimal euroAmount = converter.yenToEuro(yenAmount);
                html.append("<p>").append(amount).append(" dollars are ")
                    .append(yenAmount.toPlainString()).append(" yen.</p>");
                html.append("<p>").append(yenAmount.toPlainString()).append(" yen are ")
                    .append(euroAmount.toPlainString()).append(" Euro.</p>");
            } else {
                html.append("<p>Enter a dollar amount to convert:</p>")
                    .append("<form method=\"get\">")
                    .append("<p>$ <input title=\"Amount\" type=\"text\" name=\"amount\" size=\"25\"></p><br/>")
                    .append("<input type=\"submit\" value=\"Submit\"><input type=\"reset\" value=\"Reset\">")
                    .append("</form>");
            }
        } finally {
            html.append("</body></html>");
        }
        return HttpResponse.ok(html.toString());
    }
}
