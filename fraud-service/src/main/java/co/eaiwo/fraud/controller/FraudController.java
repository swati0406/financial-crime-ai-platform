package co.eaiwo.fraud.controller;

import co.eaiwo.fraud.domain.FraudScore;
import co.eaiwo.fraud.domain.Transaction;
import co.eaiwo.fraud.repository.FraudScoreRepository;
import co.eaiwo.fraud.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FraudController {

    private final FraudScoreRepository fraudRepo;
    private final TransactionRepository txnRepo;

    public FraudController(FraudScoreRepository fraudRepo,
                           TransactionRepository txnRepo) {
        this.fraudRepo = fraudRepo;
        this.txnRepo = txnRepo;
    }

    @GetMapping("/fraud/{customerId}")
    public ResponseEntity<FraudScore> getFraudScore(
            @PathVariable String customerId) {
        return fraudRepo.findById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transactions/{customerId}")
    public List<Transaction> getTransactions(
            @PathVariable String customerId) {
        return txnRepo.findByCustomerId(customerId);
    }
}