package micronaut.examples.tutorial.cart;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller("/cart")
public class CartController {
    @Inject Cart cart;

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Cart Service</h1><p>See /cart/api endpoints</p></body></html>"; }

    @Post(uri = "/api/initialize", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Object> init(@Body CustomerRequest req) {
        try {
            if (req.customerId != null && !req.customerId.isEmpty()) cart.initialize(req.customerName, req.customerId);
            else cart.initialize(req.customerName);
            return HttpResponse.ok(Map.of("message", "Cart initialized", "customerName", req.customerName));
        } catch (BookException e) { return HttpResponse.badRequest(Map.of("error", e.getMessage())); }
    }

    @Post("/api/books/{title}")
    public HttpResponse<Object> add(@PathVariable String title) {
        cart.addBook(title);
        return HttpResponse.ok(Map.of("title", title, "cartSize", cart.getContents().size()));
    }

    @Delete("/api/books/{title}")
    public HttpResponse<Object> remove(@PathVariable String title) {
        try { cart.removeBook(title); return HttpResponse.ok(Map.of("removed", title)); }
        catch (BookException e) { return HttpResponse.badRequest(Map.of("error", e.getMessage())); }
    }

    @Get(uri = "/api", produces = MediaType.APPLICATION_JSON)
    public List<String> contents() { return cart.getContents(); }

    @Serdeable
    public static class CustomerRequest {
        public String customerName; public String customerId;
    }
}
