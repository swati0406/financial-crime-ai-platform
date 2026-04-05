package co.eaiwo.audit.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "audit", name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String customerId;
    private String performedBy;
    private String outcome;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String eventType, String customerId,
                    String performedBy, String outcome, String details) {
        this.eventType = eventType;
        this.customerId = customerId;
        this.performedBy = performedBy;
        this.outcome = outcome;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getCustomerId() { return customerId; }
    public String getPerformedBy() { return performedBy; }
    public String getOutcome() { return outcome; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}