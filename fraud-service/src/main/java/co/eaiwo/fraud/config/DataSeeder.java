package co.eaiwo.fraud.config;

import co.eaiwo.fraud.domain.FraudScore;
import co.eaiwo.fraud.domain.Transaction;
import co.eaiwo.fraud.repository.FraudScoreRepository;
import co.eaiwo.fraud.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final FraudScoreRepository fraudRepo;
    private final TransactionRepository txnRepo;

    public DataSeeder(FraudScoreRepository fraudRepo,
                      TransactionRepository txnRepo) {
        this.fraudRepo = fraudRepo;
        this.txnRepo = txnRepo;
    }

    @Override
    public void run(String... args) {
        if (fraudRepo.count() == 0) {
            fraudRepo.saveAll(List.of(
                    new FraudScore("C123", 42, "MEDIUM",
                            "Elevated frequency,Some medium-risk payments"),
                    new FraudScore("C456", 65, "HIGH",
                            "Multiple transactions below threshold,Unusual pattern"),
                    new FraudScore("C789", 15, "LOW",
                            "Clean history,Low velocity,Normal patterns"),
                    new FraudScore("C999", 87, "CRITICAL",
                            "Structuring detected,High-risk jurisdiction,Rapid movement"),
                    new FraudScore("C321", 38, "MEDIUM",
                            "Slightly elevated activity,Minor anomalies")
            ));
            System.out.println("FraudService — seeded 5 fraud scores");
        }

        if (txnRepo.count() == 0) {
            txnRepo.saveAll(List.of(
                    new Transaction("TXN001", "C999", 15000.00, "GBP",
                            "INTERNATIONAL", "COMPLETED",
                            "Wire transfer to Deutsche Bank Frankfurt"),
                    new Transaction("TXN002", "C999", 9800.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "CHAPS payment — just below threshold"),
                    new Transaction("TXN003", "C999", 9500.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "CHAPS payment — round sum pattern"),
                    new Transaction("TXN004", "C123", 500.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "Regular standing order — utilities"),
                    new Transaction("TXN005", "C999", 25000.00, "USD",
                            "INTERNATIONAL", "FLAGGED",
                            "Large international transfer — pending review"),
                    new Transaction("TXN006", "C456", 8900.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "Payment just below £10k threshold"),
                    new Transaction("TXN007", "C456", 8750.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "Second payment just below threshold — pattern detected"),
                    new Transaction("TXN008", "C789", 250.00, "GBP",
                            "DOMESTIC", "COMPLETED",
                            "Normal low-value payment")
            ));
            System.out.println("FraudService — seeded 8 transactions");
        }
    }
}