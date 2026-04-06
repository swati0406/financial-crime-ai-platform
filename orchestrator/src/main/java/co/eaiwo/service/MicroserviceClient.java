/*
package co.eaiwo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class MicroserviceClient {

    private final WebClient customerClient;
    private final WebClient fraudClient;
    private final WebClient sanctionsClient;
    private final WebClient paymentClient;
    private final WebClient auditClient;

    public MicroserviceClient(
            @Value("${services.customer}") String customerUrl,
            @Value("${services.fraud}") String fraudUrl,
            @Value("${services.sanctions}") String sanctionsUrl,
            @Value("${services.payment}") String paymentUrl,
            @Value("${services.audit}") String auditUrl) {

        this.customerClient  = WebClient.create(customerUrl);
        this.fraudClient     = WebClient.create(fraudUrl);
        this.sanctionsClient = WebClient.create(sanctionsUrl);
        this.paymentClient   = WebClient.create(paymentUrl);
        this.auditClient     = WebClient.create(auditUrl);
    }

    // ── Customer ──────────────────────────────────────────────────

    public Map getCustomerProfile(String customerId) {
        return customerClient.get()
                .uri("/customers/" + customerId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // ── Fraud ─────────────────────────────────────────────────────

    public Map getFraudScore(String customerId) {
        return fraudClient.get()
                .uri("/fraud/" + customerId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Object getTransactionHistory(String customerId) {
        return fraudClient.get()
                .uri("/transactions/" + customerId)
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();
    }

    // ── Sanctions ─────────────────────────────────────────────────

    public Map sanctionsCheck(String name) {
        return sanctionsClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sanctions/screen")
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    // ── Payment ───────────────────────────────────────────────────

    public Object getPaymentsByCustomer(String customerId) {
        return paymentClient.get()
                .uri("/payments/customer/" + customerId)
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();
    }

    public Object getFlaggedPayments() {
        return paymentClient.get()
                .uri("/payments/flagged")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();
    }

    // ── Audit ─────────────────────────────────────────────────────

    public Map createAuditLog(String eventType, String customerId,
                              String performedBy, String outcome,
                              String details) {
        Map<String, String> body = Map.of(
                "eventType", eventType,
                "customerId", customerId,
                "performedBy", performedBy,
                "outcome", outcome,
                "details", details
        );
        return auditClient.post()
                .uri("/audit/log")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map createAlert(String alertType, String severity,
                           String customerId, String recipient,
                           String message) {
        Map<String, String> body = Map.of(
                "alertType", alertType,
                "severity", severity,
                "customerId", customerId,
                "recipient", recipient,
                "message", message,
                "status", "SENT"
        );
        return auditClient.post()
                .uri("/audit/alert")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}*/

package co.eaiwo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MicroserviceClient {

    private final JdbcTemplate jdbc;

    public MicroserviceClient(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map getCustomerProfile(String customerId) {
        try {
            return jdbc.queryForMap(
                    "SELECT * FROM customer.customers WHERE customer_id = ?", customerId);
        } catch (Exception e) {
            return Map.of("error", "Customer not found: " + customerId);
        }
    }

    public Map getFraudScore(String customerId) {
        try {
            return jdbc.queryForMap(
                    "SELECT * FROM fraud.fraud_scores WHERE customer_id = ?", customerId);
        } catch (Exception e) {
            return Map.of("error", "Fraud score not found: " + customerId);
        }
    }

    public Object getTransactionHistory(String customerId) {
        return jdbc.queryForList(
                "SELECT * FROM fraud.transactions WHERE customer_id = ?", customerId);
    }

    public Map sanctionsCheck(String name) {
        List<Map<String, Object>> matches = jdbc.queryForList(
                "SELECT * FROM sanctions.sanctions_list WHERE LOWER(entity_name) LIKE LOWER(?) AND status = 'ACTIVE'",
                "%" + name + "%");
        if (matches.isEmpty()) {
            return Map.of("status", "CLEAR", "message", name + " — no matches found across OFAC SDN, UK Consolidated, UN Security Council and EU sanctions lists.");
        }
        Map<String, Object> match = matches.get(0);
        return Map.of(
                "status", "MATCH",
                "entityName", match.get("entity_name"),
                "listType", match.get("list_type"),
                "country", match.get("country"),
                "reason", match.get("reason")
        );
    }

    public Object getPaymentsByCustomer(String customerId) {
        return jdbc.queryForList(
                "SELECT * FROM payment.payments WHERE customer_id = ?", customerId);
    }

    public Object getFlaggedPayments() {
        return jdbc.queryForList(
                "SELECT * FROM payment.payments WHERE status IN ('FLAGGED','BLOCKED') ORDER BY risk_score DESC");
    }

    public Map createAuditLog(String eventType, String customerId,
                              String performedBy, String outcome, String details) {
        jdbc.update(
                "INSERT INTO audit.audit_logs (event_type, customer_id, performed_by, outcome, details) VALUES (?,?,?,?,?)",
                eventType, customerId, performedBy, outcome, details);
        return Map.of("status", "LOGGED", "customerId", customerId, "eventType", eventType);
    }

    public Map createAlert(String alertType, String severity,
                           String customerId, String recipient, String message) {
        jdbc.update(
                "INSERT INTO audit.alerts (alert_type, severity, customer_id, recipient, message, status) VALUES (?,?,?,?,?,?)",
                alertType, severity, customerId, recipient, message, "SENT");
        return Map.of("status", "SENT", "alertType", alertType, "customerId", customerId);
    }
}