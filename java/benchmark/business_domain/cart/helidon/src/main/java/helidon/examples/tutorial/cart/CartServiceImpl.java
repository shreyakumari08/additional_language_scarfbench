package helidon.examples.tutorial.cart;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

// DEGRADED: original was @Stateful EJB / session-scope. Helidon MP CDI has SessionScoped but not for pure REST without servlet session.
@ApplicationScoped
public class CartServiceImpl implements Cart {
    private String customerId; private String customerName;
    private List<String> contents = new ArrayList<>();

    @Override public void initialize(String person) throws BookException {
        if (person == null) throw new BookException("Null person not allowed.");
        customerName = person; customerId = "0"; contents = new ArrayList<>();
    }
    @Override public void initialize(String person, String id) throws BookException {
        if (person == null) throw new BookException("Null person not allowed.");
        customerName = person;
        IdVerifier v = new IdVerifier();
        if (!v.validate(id)) throw new BookException("Invalid id: " + id);
        customerId = id; contents = new ArrayList<>();
    }
    @Override public void addBook(String title) { contents.add(title); }
    @Override public void removeBook(String title) throws BookException {
        if (!contents.remove(title)) throw new BookException("\"" + title + "\" not in cart.");
    }
    @Override public List<String> getContents() { return contents; }
    @Override public void remove() { contents.clear(); customerId = null; customerName = null; }
}
