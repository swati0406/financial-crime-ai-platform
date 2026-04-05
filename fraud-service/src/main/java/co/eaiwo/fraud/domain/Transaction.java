package co.eaiwo.fraud.domain;

import jakarta.persistence.*;

@Entity
@Table(schema = "fraud", name = "transactions")
public class Transaction {

    @Id
    private String transactionId;
    private String customerId;
    private double amount;
    private String currency;
    private String type;
    private String status;
    private String description;

    public Transaction() {}

    public Transaction(String transactionId, String customerId,
                       double amount, String currency,
                       String type, String status, String description) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public String getTransactionId() { return transactionId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
}