package co.eaiwo.sanctions.config;

import co.eaiwo.sanctions.domain.SanctionedEntity;
import co.eaiwo.sanctions.repository.SanctionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SanctionsRepository repo;

    public DataSeeder(SanctionsRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.saveAll(List.of(
                    new SanctionedEntity("Viktor Petrov", "INDIVIDUAL",
                            "OFAC_SDN", "Russia",
                            "Involvement in destabilising activities", "ACTIVE"),
                    new SanctionedEntity("Volkov Trading Ltd", "ORGANISATION",
                            "UK_CONSOLIDATED", "Russia",
                            "Sanctions evasion network", "ACTIVE"),
                    new SanctionedEntity("Alexei Morozov", "INDIVIDUAL",
                            "UK_CONSOLIDATED", "Russia",
                            "Financing prohibited activities", "ACTIVE"),
                    new SanctionedEntity("Tehran Exchange Corp", "ORGANISATION",
                            "OFAC_SDN", "Iran",
                            "Financing weapons programme", "ACTIVE"),
                    new SanctionedEntity("Pyongyang Finance Bureau", "ORGANISATION",
                            "UN_SECURITY_COUNCIL", "North Korea",
                            "WMD financing", "ACTIVE"),
                    new SanctionedEntity("Boris Ivanov", "INDIVIDUAL",
                            "UK_CONSOLIDATED", "Russia",
                            "Links to sanctioned oligarchs", "ACTIVE"),
                    new SanctionedEntity("Minsk Capital Group", "ORGANISATION",
                            "EU", "Belarus",
                            "Supporting Lukashenko regime", "ACTIVE"),
                    new SanctionedEntity("Irina Volkov", "INDIVIDUAL",
                            "OFAC_SDN", "Russia",
                            "Shell company ownership", "ACTIVE"),
                    new SanctionedEntity("Global Resources Myanmar", "ORGANISATION",
                            "UK_CONSOLIDATED", "Myanmar",
                            "Financing military junta", "ACTIVE"),
                    new SanctionedEntity("Natalia Kozlov", "INDIVIDUAL",
                            "EU", "Russia",
                            "Destabilising Ukraine", "ACTIVE"),
                    new SanctionedEntity("Dmitri Volkov", "INDIVIDUAL",
                            "OFAC_SDN", "Russia",
                            "Oligarch with Kremlin links", "ACTIVE"),
                    new SanctionedEntity("Cayman Shell Holdings", "ORGANISATION",
                            "OFAC_SDN", "Cayman Islands",
                            "Money laundering vehicle", "ACTIVE"),
                    new SanctionedEntity("Pavel Sokolov", "INDIVIDUAL",
                            "UK_CONSOLIDATED", "Russia",
                            "Sanctions circumvention", "ACTIVE"),
                    new SanctionedEntity("Syrian State Bank", "ORGANISATION",
                            "UN_SECURITY_COUNCIL", "Syria",
                            "Financing Assad regime", "ACTIVE"),
                    new SanctionedEntity("Cuba Export Finance", "ORGANISATION",
                            "OFAC_SDN", "Cuba",
                            "US embargo violation", "ACTIVE")
            ));
            System.out.println("SanctionsService — seeded 15 sanctioned entities");
        }
    }
}