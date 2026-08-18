package helidon.examples.tutorial.cart;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/cart") @ApplicationScoped
public class CartResource {
    @Inject Cart cart;

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Cart Service</h1></body></html>"; }

    public static class CustomerRequest { public String customerName; public String customerId; }

    @POST @Path("/api/initialize") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Response init(CustomerRequest req) {
        try {
            if (req.customerId != null && !req.customerId.isEmpty()) cart.initialize(req.customerName, req.customerId);
            else cart.initialize(req.customerName);
            return Response.ok(Map.of("message", "Cart initialized", "customerName", req.customerName)).build();
        } catch (BookException e) { return Response.status(400).entity(Map.of("error", e.getMessage())).build(); }
    }

    @POST @Path("/api/books/{title}") @Produces(MediaType.APPLICATION_JSON)
    public Response add(@PathParam("title") String title) {
        cart.addBook(title);
        return Response.ok(Map.of("title", title, "cartSize", cart.getContents().size())).build();
    }

    @DELETE @Path("/api/books/{title}") @Produces(MediaType.APPLICATION_JSON)
    public Response removeBook(@PathParam("title") String title) {
        try { cart.removeBook(title); return Response.ok(Map.of("removed", title)).build(); }
        catch (BookException e) { return Response.status(400).entity(Map.of("error", e.getMessage())).build(); }
    }

    @GET @Path("/api") @Produces(MediaType.APPLICATION_JSON)
    public List<String> contents() { return cart.getContents(); }
}
