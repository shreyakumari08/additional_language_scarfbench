package helidon.order;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import helidon.order.entity.Vendor;
import helidon.order.entity.VendorPart;
import helidon.order.entity.Part;
import helidon.order.entity.CustomerOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/") @ApplicationScoped
public class OrderResource {
    @PersistenceContext(unitName = "order") EntityManager em;

    @GET @Produces(MediaType.TEXT_HTML)
    public String root() { return "<html><body><h1>Order Service</h1></body></html>"; }

    @POST @Path("/init") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public Map<String,Object> init() {
        Vendor v = new Vendor(1, "Acme", "123 Main", "John", "555-1234");
        em.persist(v);
        Part p = new Part("P001", 1, "Widget", new Date(), "spec", null);
        em.persist(p);
        VendorPart vp = new VendorPart("Widget-desc", 9.99, p);
        vp.setVendor(v); em.persist(vp);
        CustomerOrder co = new CustomerOrder(1, 'N', 0, "ship-info");
        em.persist(co);
        em.flush();
        return Map.of("vendors", 1, "parts", 1, "vendorParts", 1, "orders", 1);
    }

    @GET @Path("/vendors") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> vendors() {
        return em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList().stream()
            .<Map<String,Object>>map(v -> Map.of("id", v.getVendorId(), "name", v.getName())).collect(Collectors.toList());
    }

    @GET @Path("/orders") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> orders() {
        return em.createNamedQuery("findAllOrders", CustomerOrder.class).getResultList().stream()
            .<Map<String,Object>>map(o -> Map.of("orderId", o.getOrderId(), "status", String.valueOf(o.getStatus()))).collect(Collectors.toList());
    }

    @GET @Path("/parts") @Produces(MediaType.APPLICATION_JSON) @Transactional
    public List<Map<String,Object>> parts() {
        return em.createNamedQuery("findAllParts", Part.class).getResultList().stream()
            .<Map<String,Object>>map(p -> Map.of("partNumber", p.getPartNumber(), "revision", p.getRevision(), "description", p.getDescription())).collect(Collectors.toList());
    }
}
