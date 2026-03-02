package com.jameson.orderservice.adapter.out.persistence;

import com.jameson.orderservice.domain.model.OrderStatus;
import com.jameson.orderservice.domain.port.out.OrderStatusCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerId(String customerId);
    List<OrderEntity> findByStatus(OrderStatus status);
    long countByStatus(OrderStatus status);
    List<OrderEntity> findByCustomerIdAndStatus(String customerId, OrderStatus status);

    @Query("SELECT o FROM OrderEntity o WHERE o.totalAmount > :amount ORDER BY o.createdAt DESC")
    List<OrderEntity> findHighValueOrders(@Param("amount") BigDecimal amount);

    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
    List<OrderEntity> findRecentOrdersByCustomer(@Param("customerId") String customerId);

    boolean existsByCustomerIdAndStatus(String customerId, OrderStatus status);

    @Query("SELECT o.status AS status, COUNT(o) AS count FROM OrderEntity o GROUP BY o.status")
    List<OrderStatusCount> countOrdersByStatus();
}
