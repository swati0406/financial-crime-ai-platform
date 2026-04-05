package co.eaiwo.model;

import java.time.LocalDateTime;
import java.util.List;

public class DomainDtos {

    // ── Existing records (unchanged) ─────────────────────────────

    public record CustomerProfile(
            String customerId,
            String name,
            String segment          // Gold, Silver, Bronze
    ) {}

    public record FraudScore(
            String customerId,
            int score,              // 0-100
            List<String> reasons
    ) {}

    public record NotificationResult(
            String status,
            String to
    ) {}

    // ── NEW: Customer Screening ───────────────────────────────────

    public record PEPResult(
            String customerId,
            boolean isPEP,
            String riskLevel,       // LOW, MEDIUM, HIGH
            String details,
            String screenedAgainst  // e.g. "Dow Jones Watchlist"
    ) {}

    public record KYCResult(
            String customerId,
            String status,          // PASS, FAIL, PENDING
            List<String> checksPerformed,
            String notes
    ) {}

    // ── NEW: Payment Screening ────────────────────────────────────

    public record SanctionsResult(
            String entityId,
            String status,          // CLEAR, MATCH, POTENTIAL_MATCH
            List<String> listsScreened,
            String details,
            LocalDateTime screenedAt
    ) {}

    public record PaymentRisk(
            String paymentId,
            String customerId,
            double amount,
            String currency,
            String destinationCountry,
            String riskLevel,       // LOW, MEDIUM, HIGH, CRITICAL
            int riskScore,          // 0-100
            List<String> riskFactors
    ) {}

    // ── NEW: Fraud Detection ──────────────────────────────────────

    public record Transaction(
            String transactionId,
            double amount,
            String currency,
            String type,            // DOMESTIC, INTERNATIONAL
            String status,          // COMPLETED, PENDING, FLAGGED
            String description
    ) {}

    public record TransactionHistory(
            String customerId,
            List<Transaction> transactions,
            int totalCount,
            String riskPattern     // NORMAL, UNUSUAL, SUSPICIOUS
    ) {}

    // ── NEW: Compliance ───────────────────────────────────────────

    public record SARGuidance(
            String scenario,
            String recommendation,  // SUBMIT_SAR, MONITOR, NO_ACTION
            String legalBasis,
            List<String> requiredActions,
            String timeframe
    ) {}

    // ── NEW: Notification & Audit ─────────────────────────────────

    public record AuditLog(
            String logId,
            String eventType,       // FRAUD_CHECK, SANCTIONS_SCREEN, SAR_SUBMITTED
            String customerId,
            String performedBy,     // agent name
            String outcome,
            LocalDateTime timestamp
    ) {}

    public record AlertNotification(
            String alertId,
            String type,            // FRAUD_ALERT, SANCTIONS_MATCH, SAR_REQUIRED
            String severity,        // LOW, MEDIUM, HIGH, CRITICAL
            String recipient,
            String message,
            String status           // SENT, FAILED
    ) {}
}