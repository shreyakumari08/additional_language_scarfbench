package micronaut.coffeeshop;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// DEGRADED: original was multi-service coffee-shop (~61 KLOC) with async ordering, JMS routing.
// Single-verticle REST: menu + orders. Async ordering not preserved (would need Kafka).
@Controller
public class CoffeeController {
    private final java.util.Map<String, Map<String,Object>> orders = new ConcurrentHashMap<>();

    @Get(produces = MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Coffee Shop</h1><p>/menu /orders</p></body></html>"; }

    @Get(uri = "/menu", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> menu() {
        return List.of(
            Map.of("id", "espresso", "name", "Espresso", "price", 3.5),
            Map.of("id", "latte", "name", "Latte", "price", 4.5),
            Map.of("id", "cappuccino", "name", "Cappuccino", "price", 4.0)
        );
    }

    @Post(uri = "/orders", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Map<String,Object> order(@Body OrderRequest req) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> o = Map.of("id", id, "item", req.item, "status", "queued");
        orders.put(id, o);
        return o;
    }

    @Get(uri = "/orders", produces = MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> allOrders() { return List.copyOf(orders.values()); }

    @Serdeable
    public static class OrderRequest { public String item; }
}
