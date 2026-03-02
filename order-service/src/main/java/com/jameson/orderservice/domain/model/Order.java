package com.jameson.orderservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private Long id;
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private FraudScore fraudScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order() {
    }

    public Order(String customerId, String productId, Integer quantity, BigDecimal totalAmount) {
        validateInputs(customerId, productId, quantity, totalAmount);

        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.fraudScore = null; // THIS Will be set by fraud detection
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Order reconstruct(Long id, String customerId, String productId,
                                    Integer quantity, BigDecimal totalAmount,
                                    OrderStatus status, FraudScore fraudScore,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        Order order = new Order();
        order.id = id;
        order.customerId = customerId;
        order.productId = productId;
        order.quantity = quantity;
        order.totalAmount = totalAmount;
        order.status = status;
        order.fraudScore = fraudScore;
        order.createdAt = createdAt;
        order.updatedAt =updatedAt;
        return order;
    }

    public void markAsSuspicious(FraudScore fraudScore) {
        if (fraudScore == null ) {
            throw new IllegalArgumentException("Fraud score cannot be null");
        }

        this.fraudScore = fraudScore;

        if (fraudScore.isHighRisk()) {
            this.status = OrderStatus.PENDING_REVIEW;
        } else if (fraudScore.isMediumRisk()) {
            this.status = OrderStatus.PENDING_VERIFICATION;
        }

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Confirm the order after inventory validation.
     */
    public void confirm() {
        if (!canBeConfirmed()) {
            throw new IllegalStateException(
                    String.format("Order %d cannot be confirmed. Current status: %s", id, status)
            );
        }

        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        this.status = OrderStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException(
                    String.format("Order %d cannot be cancelled. current status: %s", id, status)
            );
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void process() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed orders can be processed");
        }

        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void ship() {
        if (this.status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Only processing orders can be shipped");
        }

        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be completed");
        }

        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== Business Rules ====================
    public boolean canBeConfirmed() {
        return this.status == OrderStatus.PENDING
                || this.status == OrderStatus.PENDING_VERIFICATION;
    }

    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING
                || this.status == OrderStatus.PENDING_REVIEW
                || this.status == OrderStatus.PENDING_VERIFICATION
                || this.status == OrderStatus.CONFIRMED;
    }

    public boolean isFinalState() {
        return this.status == OrderStatus.COMPLETED
                || this.status == OrderStatus.CANCELLED
                || this.status == OrderStatus.REJECTED;
    }

    public BigDecimal calculateFinalAmount() {
        // Future: Apply discounts, taxes, shipping costs
        return this.totalAmount;
    }

    private void validateInputs(String customerId, String productId, Integer quantity, BigDecimal totalAmount) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot exceed 100 items per order");
        }
    }

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public FraudScore getFraudScore() {
        return fraudScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

}
