package co.eaiwo.audit.config;

import co.eaiwo.audit.domain.Alert;
import co.eaiwo.audit.domain.AuditLog;
import co.eaiwo.audit.repository.AlertRepository;
import co.eaiwo.audit.repository.AuditLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuditLogRepository auditRepo;
    private final AlertRepository alertRepo;

    public DataSeeder(AuditLogRepository auditRepo,
                      AlertRepository alertRepo) {
        this.auditRepo = auditRepo;
        this.alertRepo = alertRepo;
    }

    @Override
    public void run(String... args) {
        if (auditRepo.count() == 0) {
            auditRepo.saveAll(List.of(
                    new AuditLog("SANCTIONS_SCREEN", "C999",
                            "PaymentScreeningAgent",
                            "MATCH_FOUND",
                            "Customer matched on OFAC SDN list — transaction blocked"),
                    new AuditLog("FRAUD_CHECK", "C999",
                            "FraudDetectionAgent",
                            "HIGH_RISK",
                            "Fraud score 87 — structuring pattern detected"),
                    new AuditLog("KYC_CHECK", "C123",
                            "CustomerScreeningAgent",
                            "PASS",
                            "All KYC checks passed — identity verified"),
                    new AuditLog("SAR_SUBMITTED", "C031",
                            "ComplianceAgent",
                            "SUBMITTED",
                            "SAR filed with NCA — structuring and sanctions risk"),
                    new AuditLog("PAYMENT_BLOCKED", "C014",
                            "PaymentScreeningAgent",
                            "BLOCKED",
                            "Payment to Cayman Islands blocked — CRITICAL risk score 98"),
                    new AuditLog("PEP_SCREEN", "C019",
                            "CustomerScreeningAgent",
                            "PEP_IDENTIFIED",
                            "Customer identified as PEP — EDD required")
            ));
            System.out.println("AuditService — seeded 6 audit logs");
        }

        if (alertRepo.count() == 0) {
            alertRepo.saveAll(List.of(
                    new Alert("SANCTIONS_MATCH", "CRITICAL", "C999",
                            "compliance@bank.com",
                            "URGENT: Sanctions match found for customer C999 — Viktor Petrov. Transaction blocked immediately.",
                            "SENT"),
                    new Alert("FRAUD_ALERT", "HIGH", "C031",
                            "fraud@bank.com",
                            "High fraud risk detected for C031 — score 94. Multiple structuring transactions identified.",
                            "SENT"),
                    new Alert("SAR_REQUIRED", "CRITICAL", "C031",
                            "mlro@bank.com",
                            "SAR submission required for C031 within 24 hours. Evidence of structuring and sanctions risk.",
                            "SENT"),
                    new Alert("PEP_EDD_REQUIRED", "HIGH", "C019",
                            "compliance@bank.com",
                            "EDD required for PEP customer C019 — Tariq Al-Farsi. Source of wealth documentation needed.",
                            "SENT"),
                    new Alert("PAYMENT_BLOCKED", "CRITICAL", "C014",
                            "payments@bank.com",
                            "Payment PAY003 blocked — destination Cayman Islands, risk score 98. Immediate review required.",
                            "SENT"),
                    new Alert("TRANSACTION_MONITORING", "MEDIUM", "C456",
                            "fraud@bank.com",
                            "Unusual transaction pattern detected for C456 — multiple payments just below threshold.",
                            "SENT")
            ));
            System.out.println("AuditService — seeded 6 alerts");
        }
    }
}