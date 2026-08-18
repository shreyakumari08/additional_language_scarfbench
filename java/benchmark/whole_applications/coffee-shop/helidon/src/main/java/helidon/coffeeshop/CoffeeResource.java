package helidon.coffeeshop;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// DEGRADED: full port ~61 KLOC multi-service. Menu+orders preserved.
@Path("/") @ApplicationScoped
public class CoffeeResource {
    private final Map<String, Map<String,Object>> orders = new ConcurrentHashMap<>();

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Coffee Shop</h1></body></html>"; }

    @GET @Path("/menu") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> menu() {
        return List.of(
            Map.of("id","espresso","name","Espresso","price",3.5),
            Map.of("id","latte","name","Latte","price",4.5),
            Map.of("id","cappuccino","name","Cappuccino","price",4.0)
        );
    }

    public static class OrderRequest { public String item; }

    @POST @Path("/orders") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> order(OrderRequest req) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> o = Map.of("id", id, "item", req.item, "status", "queued");
        orders.put(id, o);
        return o;
    }

    @GET @Path("/orders") @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> allOrders() { return List.copyOf(orders.values()); }
}
