package co.eaiwo.fraud.domain;

import jakarta.persistence.*;

@Entity
@Table(schema = "fraud", name = "fraud_scores")
public class FraudScore {

    @Id
    private String customerId;
    private int score;
    private String riskLevel;
    private String reasons;

    public FraudScore() {}

    public FraudScore(String customerId, int score,
                      String riskLevel, String reasons) {
        this.customerId = customerId;
        this.score = score;
        this.riskLevel = riskLevel;
        this.reasons = reasons;
    }

    public String getCustomerId() { return customerId; }
    public int getScore() { return score; }
    public String getRiskLevel() { return riskLevel; }
    public String getReasons() { return reasons; }
}