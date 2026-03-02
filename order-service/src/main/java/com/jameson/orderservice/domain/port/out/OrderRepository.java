package com.jameson.orderservice.domain.port.out;

import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();
    long countByStatus(OrderStatus status);
    List<OrderStatusCount> countOrdersByStatus();
}
