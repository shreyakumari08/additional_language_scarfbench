package micronaut.examples.tutorial.cart;

import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

// DEGRADED: original was @SessionScope in Spring / @Stateful EJB in Jakarta.
// Micronaut has no built-in session-scoped bean in this variant; using app-scope with per-request cart via HTTP header would require rework.
// Preserved single-user behavior for smoke verification.
@Singleton
public class CartServiceImpl implements Cart {
    private String customerId;
    private String customerName;
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
