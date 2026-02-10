package com.jameson.orderservice.domain.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Order {

    private UUID id;

    private String productId;
    private String customerId;
    private Double amount;
    private Instant createdAt;

    public static Order createOrder(String productId, String customerId, Double totalAmount) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setProductId(productId);
        order.setCustomerId(customerId);
        order.setAmount(totalAmount);
        order.setCreatedAt(Instant.now());
        return order;
    }
}
