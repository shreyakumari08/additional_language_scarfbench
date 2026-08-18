package micronaut.order;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import micronaut.order.entity.Vendor;
import micronaut.order.entity.VendorPart;
import micronaut.order.entity.Part;
import micronaut.order.entity.CustomerOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    @Inject EntityManager em;

    @Get(produces = MediaType.TEXT_HTML)
    public String root() {
        return "<html><body><h1>Order Service</h1><p>7-entity JPA graph. Endpoints: /vendors /parts /orders</p></body></html>";
    }

    @Post(uri = "/init", produces = MediaType.APPLICATION_JSON)
    @Transactional
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

    @Get(uri = "/vendors", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> vendors() {
        List<Vendor> vs = em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList();
        return vs.stream().<Map<String,Object>>map(v -> Map.of("id", v.getVendorId(), "name", v.getName())).toList();
    }

    @Get(uri = "/orders", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> orders() {
        List<CustomerOrder> os = em.createNamedQuery("findAllOrders", CustomerOrder.class).getResultList();
        return os.stream().<Map<String,Object>>map(o -> Map.of("orderId", o.getOrderId(), "status", String.valueOf(o.getStatus()))).toList();
    }

    @Get(uri = "/parts", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Map<String,Object>> parts() {
        List<Part> ps = em.createNamedQuery("findAllParts", Part.class).getResultList();
        return ps.stream().<Map<String,Object>>map(p -> Map.of("partNumber", p.getPartNumber(), "revision", p.getRevision(), "description", p.getDescription())).toList();
    }
}
