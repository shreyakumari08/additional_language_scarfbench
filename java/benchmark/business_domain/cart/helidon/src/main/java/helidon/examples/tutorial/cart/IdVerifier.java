package helidon.examples.tutorial.cart;
public class IdVerifier {
    public boolean validate(String id) {
        if (id == null || id.isEmpty()) return false;
        try { Integer.parseInt(id); return true; } catch (NumberFormatException e) { return false; }
    }
}
