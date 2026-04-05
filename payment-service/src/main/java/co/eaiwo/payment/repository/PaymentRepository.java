package co.eaiwo.payment.repository;

import co.eaiwo.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, String> {

    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByStatus(String status);
    List<Payment> findByRiskLevel(String riskLevel);
}