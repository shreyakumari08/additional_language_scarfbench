package micronaut.tutorial.customer.data;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

@Entity
@Table(name = "CUSTOMER_ADDRESS")
@Serdeable
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int number;
    private String street;
    private String city;
    private String province;
    private String zip;
    private String country;

    public Address() {}
    public Long getId() { return id; }
    public int getNumber() { return number; } public void setNumber(int n) { this.number = n; }
    public String getStreet() { return street; } public void setStreet(String s) { this.street = s; }
    public String getCity() { return city; } public void setCity(String s) { this.city = s; }
    public String getProvince() { return province; } public void setProvince(String s) { this.province = s; }
    public String getZip() { return zip; } public void setZip(String s) { this.zip = s; }
    public String getCountry() { return country; } public void setCountry(String s) { this.country = s; }
}
