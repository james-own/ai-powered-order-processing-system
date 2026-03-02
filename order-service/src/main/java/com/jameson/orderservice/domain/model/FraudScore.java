package com.jameson.orderservice.domain.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class FraudScore {
    private final double score; // 0.0 to 1.0
    private final RiskLevel riskLevel;
    private final String reason;

    public FraudScore(double score, RiskLevel riskLevel, String reason) {
        validateScore(score);
        this.score = score;
        this.riskLevel = riskLevel != null ? riskLevel : determineRiskLevel(score);
        this.reason = reason != null ? reason : "Automated fraud detection";
    }

    private void validateScore(double score) {
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException(String.format("Fraud score must be between 0.0 and 1.0, got: %.2f", score));
        }
    }

    public static FraudScore fromScore(double score) {
        return new FraudScore(score, determineRiskLevel(score), null);
    }

    // Factory method for low risk orders
    public static FraudScore highRisk(String reason) {
        return new FraudScore(10.9, RiskLevel.HIGH, reason);
    }

    public static FraudScore lowRisk() {
        return new FraudScore(0.0, RiskLevel.LOW, "Low risk transaction");
    }

    private static RiskLevel determineRiskLevel(double score) {
        if (score <= 0.3) {
            return RiskLevel.LOW;
        } else if (score <= 0.7) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.HIGH;
        }
    }

    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH;
    }

    public boolean isMediumRisk() {
        return riskLevel == RiskLevel.MEDIUM;
    }

    public boolean isLowRisk() {
        return riskLevel == RiskLevel.LOW;
    }

    public boolean shouldAutoApprove() {
        return score < 0.3;
    }

    public boolean needsManualReview() {
        return score >= 0.7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FraudScore that = (FraudScore) o;
        return Double.compare(that.score, score) == 0 && riskLevel == that.riskLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, riskLevel);
    }

    @Override
    public String toString() {
        return String.format("FraudScore{score=%.2f, level=%s, reason='%s'}",
                score, riskLevel, reason);
    }

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }
}
