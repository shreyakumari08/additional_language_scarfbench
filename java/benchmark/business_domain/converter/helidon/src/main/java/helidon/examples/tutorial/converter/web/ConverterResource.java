package helidon.examples.tutorial.converter.web;

import helidon.examples.tutorial.converter.ejb.ConverterBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.math.BigDecimal;

@Path("/")
@ApplicationScoped
public class ConverterResource {

    @Inject
    ConverterBean converter;

    @Context UriInfo uri;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String convert(@QueryParam("amount") String amount) {
        StringBuilder html = new StringBuilder();
        html.append("<html lang=\"en\"><head><title>Servlet ConverterServlet</title></head>")
            .append("<body><h1>Servlet ConverterServlet at ").append(uri.getPath()).append("</h1>");
        try {
            if (amount != null && !amount.isEmpty()) {
                BigDecimal d = new BigDecimal(amount);
                BigDecimal yenAmount = converter.dollarToYen(d);
                BigDecimal euroAmount = converter.yenToEuro(yenAmount);
                html.append("<p>").append(amount).append(" dollars are ").append(yenAmount.toPlainString()).append(" yen.</p>");
                html.append("<p>").append(yenAmount.toPlainString()).append(" yen are ").append(euroAmount.toPlainString()).append(" Euro.</p>");
            } else {
                html.append("<p>Enter a dollar amount to convert:</p><form method=\"get\">")
                    .append("<p>$ <input title=\"Amount\" type=\"text\" name=\"amount\" size=\"25\"></p><br/>")
                    .append("<input type=\"submit\" value=\"Submit\"><input type=\"reset\" value=\"Reset\"></form>");
            }
        } finally { html.append("</body></html>"); }
        return html.toString();
    }
}
