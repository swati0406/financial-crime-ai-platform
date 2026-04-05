package co.eaiwo.audit.repository;

import co.eaiwo.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByCustomerId(String customerId);
    List<AuditLog> findByEventType(String eventType);
}