package com.jameson.orderservice.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * CreateOrderCommand - Command object for creating orders
 * This is an immutable command object that carries the data needed to create an order.
 * Following Command pattern and CQRS principles.
 */
public class CreateOrderCommand {
    private final String customerId;
    private final String productId;
    private final Integer quantity;
    private final BigDecimal totalAmount;

    public CreateOrderCommand(String customerId, String productId, Integer quantity, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderCommand that = (CreateOrderCommand) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, productId, quantity, totalAmount);
    }

    @Override
    public String toString() {
        return "CreateOrderCommand{" +
                "customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
