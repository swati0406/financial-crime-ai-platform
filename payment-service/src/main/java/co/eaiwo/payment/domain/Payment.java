package co.eaiwo.payment.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "payment", name = "payments")
public class Payment {

    @Id
    private String paymentId;
    private String customerId;
    private double amount;
    private String currency;
    private String destinationCountry;
    private String beneficiaryName;
    private String beneficiaryBank;
    private String paymentType;     // DOMESTIC, INTERNATIONAL, SWIFT, CHAPS
    private String status;          // PENDING, COMPLETED, FLAGGED, BLOCKED
    private String riskLevel;       // LOW, MEDIUM, HIGH, CRITICAL
    private int riskScore;
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(String paymentId, String customerId, double amount,
                   String currency, String destinationCountry,
                   String beneficiaryName, String beneficiaryBank,
                   String paymentType, String status,
                   String riskLevel, int riskScore) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.destinationCountry = destinationCountry;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryBank = beneficiaryBank;
        this.paymentType = paymentType;
        this.status = status;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.createdAt = LocalDateTime.now();
    }

    public String getPaymentId() { return paymentId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDestinationCountry() { return destinationCountry; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public String getBeneficiaryBank() { return beneficiaryBank; }
    public String getPaymentType() { return paymentType; }
    public String getStatus() { return status; }
    public String getRiskLevel() { return riskLevel; }
    public int getRiskScore() { return riskScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}