package helidon.tutorial.customer.resource;

import helidon.tutorial.customer.data.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/Customer")
@ApplicationScoped
public class CustomerResource {

    @PersistenceContext(unitName = "customer") EntityManager em;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<Customer> getAllCustomers() {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class).getResultList();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Customer getCustomer(@PathParam("id") Integer id) { return em.find(Customer.class, id); }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createCustomer(Customer customer) {
        em.persist(customer.getAddress());
        em.persist(customer);
        return Response.created(URI.create("/" + customer.getId())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateCustomer(@PathParam("id") Integer id, Customer customer) {
        Customer existing = em.find(Customer.class, id);
        if (existing == null) return Response.status(404).build();
        em.merge(customer);
        return Response.status(303).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCustomer(@PathParam("id") Integer id) {
        Customer c = em.find(Customer.class, id);
        if (c == null) return Response.status(404).build();
        if (c.getAddress() != null) em.remove(c.getAddress());
        em.remove(c);
        return Response.noContent().build();
    }
}
