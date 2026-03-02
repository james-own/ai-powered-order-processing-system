package com.jameson.orderservice.adapter.in.rest.dto;

import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private FraudScoreDto fraudScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public OrderResponse() {
    }

    // Full constructor
    public OrderResponse(Long id, String customerId, String productId, Integer quantity,
                         BigDecimal totalAmount, OrderStatus status, FraudScoreDto fraudScore,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status;
        this.fraudScore = fraudScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method to create response from domain model.
     */
    public static OrderResponse from(Order order) {
        FraudScoreDto fraudScoreDto = null;
        if (order.getFraudScore() != null) {
            fraudScoreDto = new FraudScoreDto(
                    order.getFraudScore().getScore(),
                    order.getFraudScore().getRiskLevel().name(),
                    order.getFraudScore().getReason()
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus(),
                fraudScoreDto,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public FraudScoreDto getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(FraudScoreDto fraudScore) {
        this.fraudScore = fraudScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== Nested DTO for Fraud Score ====================

    public static class FraudScoreDto {
        private double score;
        private String riskLevel;
        private String reason;

        public FraudScoreDto() {
        }

        public FraudScoreDto(double score, String riskLevel, String reason) {
            this.score = score;
            this.riskLevel = riskLevel;
            this.reason = reason;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
