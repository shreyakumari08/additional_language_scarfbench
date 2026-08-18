package helidon.tutorial.rsvp.entity;
import jakarta.persistence.*;
@Entity
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
    private String name;
    private String location;
    public Event() {} public Event(String n, String l) { this.name = n; this.location = l; }
    public Long getId() { return id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getLocation() { return location; } public void setLocation(String s) { this.location = s; }
}
