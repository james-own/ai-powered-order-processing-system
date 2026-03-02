package com.jameson.orderservice.domain.port.in;

import com.jameson.orderservice.adapter.in.rest.dto.OrderStatsResponse;
import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * GetOrderUseCase - Input Port for querying orders
 * This defines the query operations available in the application.
 */
public interface GetOrderUseCase {
    Optional<Order> getOrderById(Long id);
    List<Order> getOrdersByCustomerId(String customerId);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getAllOrders();
    OrderStatsResponse getOrderStats();
}