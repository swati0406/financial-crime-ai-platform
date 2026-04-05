package co.eaiwo.customer.domain;

import jakarta.persistence.*;

@Entity
@Table(schema = "customer", name = "customers")
public class Customer {

    @Id
    private String customerId;
    private String name;
    private String segment;
    private String email;
    private String phone;
    private String address;
    private String nationality;
    private String riskRating;
    private boolean active;

    public Customer() {}

    public Customer(String customerId, String name, String segment,
                    String email, String phone, String address,
                    String nationality, String riskRating, boolean active) {
        this.customerId = customerId;
        this.name = name;
        this.segment = segment;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.nationality = nationality;
        this.riskRating = riskRating;
        this.active = active;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getSegment() { return segment; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getNationality() { return nationality; }
    public String getRiskRating() { return riskRating; }
    public boolean isActive() { return active; }
}
