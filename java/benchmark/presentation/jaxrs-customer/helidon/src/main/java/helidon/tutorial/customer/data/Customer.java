package helidon.tutorial.customer.data;
import jakarta.persistence.*;

@Entity
@Table(name = "CUSTOMER_CUSTOMER")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private Integer id;
    private String firstname; private String lastname;
    @OneToOne(cascade = CascadeType.ALL) private Address address;
    private String email; private String phone;
    public Customer() { address = new Address(); }
    public Integer getId() { return id; }
    public String getFirstname() { return firstname; } public void setFirstname(String s) { this.firstname = s; }
    public String getLastname() { return lastname; } public void setLastname(String s) { this.lastname = s; }
    public Address getAddress() { return address; } public void setAddress(Address a) { this.address = a; }
    public String getEmail() { return email; } public void setEmail(String s) { this.email = s; }
    public String getPhone() { return phone; } public void setPhone(String s) { this.phone = s; }
}
