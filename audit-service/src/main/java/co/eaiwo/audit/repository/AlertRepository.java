package co.eaiwo.audit.repository;

import co.eaiwo.audit.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository
        extends JpaRepository<Alert, Long> {

    List<Alert> findBySeverity(String severity);
    List<Alert> findByCustomerId(String customerId);
    List<Alert> findByStatus(String status);
}