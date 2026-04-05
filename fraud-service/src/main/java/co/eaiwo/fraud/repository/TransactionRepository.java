package co.eaiwo.fraud.repository;

import co.eaiwo.fraud.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    List<Transaction> findByCustomerId(String customerId);
}