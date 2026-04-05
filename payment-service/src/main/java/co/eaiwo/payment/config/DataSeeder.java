package co.eaiwo.payment.config;

import co.eaiwo.payment.domain.Payment;
import co.eaiwo.payment.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PaymentRepository repo;

    public DataSeeder(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.saveAll(List.of(
                    new Payment("PAY001", "C999", 48000.00, "GBP",
                            "Russia", "Viktor Petrov Jr", "Sberbank Moscow",
                            "SWIFT", "FLAGGED", "CRITICAL", 95),
                    new Payment("PAY002", "C999", 9900.00, "GBP",
                            "UK", "Cash Withdrawal", "HSBC London",
                            "DOMESTIC", "COMPLETED", "HIGH", 72),
                    new Payment("PAY003", "C014", 75000.00, "USD",
                            "Cayman Islands", "Cayman Shell Holdings",
                            "Cayman National Bank",
                            "SWIFT", "FLAGGED", "CRITICAL", 98),
                    new Payment("PAY004", "C023", 55000.00, "EUR",
                            "Iran", "Tehran Exchange Corp",
                            "Bank Mellat Tehran",
                            "SWIFT", "BLOCKED", "CRITICAL", 100),
                    new Payment("PAY005", "C123", 1200.00, "GBP",
                            "UK", "British Gas", "Barclays London",
                            "DOMESTIC", "COMPLETED", "LOW", 8),
                    new Payment("PAY006", "C123", 2500.00, "GBP",
                            "UK", "HMRC Tax", "NatWest London",
                            "CHAPS", "COMPLETED", "LOW", 5),
                    new Payment("PAY007", "C456", 8900.00, "GBP",
                            "UK", "Property Management Ltd", "Lloyds London",
                            "CHAPS", "FLAGGED", "HIGH", 68),
                    new Payment("PAY008", "C456", 8750.00, "GBP",
                            "UK", "Investment Holdings Ltd", "Santander UK",
                            "CHAPS", "FLAGGED", "HIGH", 71),
                    new Payment("PAY009", "C031", 120000.00, "USD",
                            "Panama", "Global Resources Inc",
                            "Banco Nacional Panama",
                            "SWIFT", "FLAGGED", "CRITICAL", 97),
                    new Payment("PAY010", "C039", 67000.00, "GBP",
                            "Russia", "Volkov Trading Ltd",
                            "VTB Bank Moscow",
                            "SWIFT", "BLOCKED", "CRITICAL", 99),
                    new Payment("PAY011", "C789", 500.00, "GBP",
                            "UK", "Tesco PLC", "Halifax London",
                            "DOMESTIC", "COMPLETED", "LOW", 3),
                    new Payment("PAY012", "C019", 85000.00, "USD",
                            "UAE", "Al Maktoum Holdings",
                            "Emirates NBD Dubai",
                            "SWIFT", "FLAGGED", "HIGH", 78),
                    new Payment("PAY013", "C003", 45000.00, "GBP",
                            "Saudi Arabia", "Riyadh Investment Corp",
                            "Al Rajhi Bank",
                            "SWIFT", "COMPLETED", "MEDIUM", 52),
                    new Payment("PAY014", "C028", 32000.00, "GBP",
                            "Belarus", "Minsk Capital Group",
                            "Belarusbank Minsk",
                            "SWIFT", "BLOCKED", "CRITICAL", 96),
                    new Payment("PAY015", "C005", 9850.00, "GBP",
                            "UK", "London Properties Ltd", "HSBC London",
                            "CHAPS", "COMPLETED", "HIGH", 74)
            ));
            System.out.println("PaymentService — seeded 15 payments");
        }
    }
}