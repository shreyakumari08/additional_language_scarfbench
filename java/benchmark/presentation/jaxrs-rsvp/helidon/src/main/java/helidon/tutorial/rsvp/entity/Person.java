package helidon.tutorial.rsvp.entity;
import jakarta.persistence.*;
@Entity
public class Person {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
    private String name;
    public Person() {} public Person(String n) { this.name = n; }
    public Long getId() { return id; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
}
