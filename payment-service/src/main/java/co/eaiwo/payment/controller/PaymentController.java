package co.eaiwo.payment.controller;

import co.eaiwo.payment.domain.Payment;
import co.eaiwo.payment.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository repo;

    public PaymentController(PaymentRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getById(
            @PathVariable String paymentId) {
        return repo.findById(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<Payment> getByCustomer(
            @PathVariable String customerId) {
        return repo.findByCustomerId(customerId);
    }

    @GetMapping("/flagged")
    public List<Payment> getFlagged() {
        return repo.findByStatus("FLAGGED");
    }

    @GetMapping("/high-risk")
    public List<Payment> getHighRisk() {
        return repo.findByRiskLevel("HIGH");
    }

    @PostMapping
    public Payment create(@RequestBody Payment payment) {
        return repo.save(payment);
    }
}