package co.eaiwo.sanctions.controller;

import co.eaiwo.sanctions.domain.SanctionedEntity;
import co.eaiwo.sanctions.repository.SanctionsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sanctions")
public class SanctionsController {

    private final SanctionsRepository repo;

    public SanctionsController(SanctionsRepository repo) {
        this.repo = repo;
    }

    // Screen a name against the sanctions list
    @GetMapping("/screen")
    public Map<String, Object> screen(@RequestParam String name) {
        List<SanctionedEntity> matches = repo.searchByName(name);
        String status = matches.isEmpty() ? "CLEAR" : "MATCH";
        return Map.of(
                "name", name,
                "status", status,
                "matches", matches.size(),
                "details", matches
        );
    }

    // Get all sanctioned entities
    @GetMapping
    public List<SanctionedEntity> getAll() {
        return repo.findAll();
    }
}