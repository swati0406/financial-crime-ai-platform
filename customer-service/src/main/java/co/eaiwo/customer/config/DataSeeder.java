package co.eaiwo.customer.config;

import co.eaiwo.customer.domain.Customer;
import co.eaiwo.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository repo;

    public DataSeeder(CustomerRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.saveAll(List.of(
                    new Customer("C123", "Alex Morgan", "Gold",
                            "alex.morgan@example.com", "+44 7700 900001",
                            "12 High Street, London EC1A 1BB",
                            "British", "LOW", true),
                    new Customer("C456", "Sarah Chen", "Silver",
                            "sarah.chen@example.com", "+44 7700 900002",
                            "45 Park Lane, Manchester M1 2AB",
                            "British-Chinese", "MEDIUM", true),
                    new Customer("C789", "James Okafor", "Bronze",
                            "james.okafor@example.com", "+44 7700 900003",
                            "78 Queen Street, Birmingham B1 3CD",
                            "British-Nigerian", "LOW", true),
                    new Customer("C999", "Viktor Petrov", "Gold",
                            "viktor.petrov@example.com", "+44 7700 900004",
                            "3 Mayfair Place, London W1J 8AJ",
                            "Russian", "HIGH", true),
                    new Customer("C321", "Aisha Al-Mansouri", "Silver",
                            "aisha.almansouri@example.com", "+44 7700 900005",
                            "22 Canal Street, Leeds LS1 4AB",
                            "Emirati", "MEDIUM", true)
            ));
            System.out.println("CustomerService — seeded 5 customers");
        }
    }
}
