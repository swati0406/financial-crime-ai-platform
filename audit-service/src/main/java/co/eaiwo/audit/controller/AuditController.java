package co.eaiwo.audit.controller;

import co.eaiwo.audit.domain.Alert;
import co.eaiwo.audit.domain.AuditLog;
import co.eaiwo.audit.repository.AlertRepository;
import co.eaiwo.audit.repository.AuditLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuditController {

    private final AuditLogRepository auditRepo;
    private final AlertRepository alertRepo;

    public AuditController(AuditLogRepository auditRepo,
                           AlertRepository alertRepo) {
        this.auditRepo = auditRepo;
        this.alertRepo = alertRepo;
    }

    @PostMapping("/audit/log")
    public AuditLog createLog(@RequestBody AuditLog log) {
        return auditRepo.save(log);
    }

    @GetMapping("/audit/logs")
    public List<AuditLog> getAllLogs() {
        return auditRepo.findAll();
    }

    @GetMapping("/audit/logs/customer/{customerId}")
    public List<AuditLog> getLogsByCustomer(
            @PathVariable String customerId) {
        return auditRepo.findByCustomerId(customerId);
    }

    @PostMapping("/audit/alert")
    public Alert createAlert(@RequestBody Alert alert) {
        return alertRepo.save(alert);
    }

    @GetMapping("/audit/alerts")
    public List<Alert> getAllAlerts() {
        return alertRepo.findAll();
    }

    @GetMapping("/audit/alerts/critical")
    public List<Alert> getCriticalAlerts() {
        return alertRepo.findBySeverity("CRITICAL");
    }

    @GetMapping("/audit/alerts/customer/{customerId}")
    public List<Alert> getAlertsByCustomer(
            @PathVariable String customerId) {
        return alertRepo.findByCustomerId(customerId);
    }
}