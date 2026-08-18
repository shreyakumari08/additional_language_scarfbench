package micronaut.tutorial.customer.resource;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import micronaut.tutorial.customer.data.Customer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller("/Customer")
@Singleton
public class CustomerResource {

    private static final Logger logger = Logger.getLogger(CustomerResource.class.getName());
    @Inject EntityManager em;

    @Get(uri = "/all", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public List<Customer> getAllCustomers() {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class).getResultList();
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Transactional
    public Customer getCustomer(@PathVariable Integer id) {
        return em.find(Customer.class, id);
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    @Transactional
    public HttpResponse<Void> createCustomer(@Body Customer customer) {
        try {
            em.persist(customer.getAddress());
            em.persist(customer);
            return HttpResponse.created(java.net.URI.create("/" + customer.getId()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating customer: {0}", e.getMessage());
            return HttpResponse.serverError();
        }
    }

    @Put(uri = "/{id}", consumes = MediaType.APPLICATION_JSON)
    @Transactional
    public HttpResponse<Void> updateCustomer(@PathVariable Integer id, @Body Customer customer) {
        Customer existing = em.find(Customer.class, id);
        if (existing == null) return HttpResponse.notFound();
        em.merge(customer);
        return HttpResponse.status(io.micronaut.http.HttpStatus.SEE_OTHER);
    }

    @Delete("/{id}")
    @Transactional
    public HttpResponse<Void> deleteCustomer(@PathVariable Integer id) {
        Customer c = em.find(Customer.class, id);
        if (c == null) return HttpResponse.notFound();
        if (c.getAddress() != null) em.remove(c.getAddress());
        em.remove(c);
        return HttpResponse.noContent();
    }
}
