package co.eaiwo.audit.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "audit", name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String alertType;
    private String severity;
    private String customerId;
    private String recipient;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    public Alert() {}

    public Alert(String alertType, String severity, String customerId,
                 String recipient, String message, String status) {
        this.alertType = alertType;
        this.severity = severity;
        this.customerId = customerId;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAlertType() { return alertType; }
    public String getSeverity() { return severity; }
    public String getCustomerId() { return customerId; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}