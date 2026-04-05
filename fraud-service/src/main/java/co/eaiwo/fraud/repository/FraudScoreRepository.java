package co.eaiwo.fraud.repository;

import co.eaiwo.fraud.domain.FraudScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudScoreRepository
        extends JpaRepository<FraudScore, String> {}