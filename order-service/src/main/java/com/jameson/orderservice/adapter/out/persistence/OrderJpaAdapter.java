package com.jameson.orderservice.adapter.out.persistence;

import com.jameson.orderservice.application.mapper.OrderMapper;
import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;
import com.jameson.orderservice.domain.port.out.OrderRepository;
import com.jameson.orderservice.domain.port.out.OrderStatusCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderJpaAdapter implements OrderRepository {

    private static final Logger  log = LoggerFactory.getLogger(OrderJpaAdapter.class);

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    public OrderJpaAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        log.debug("Saving order: {}", order);

        OrderEntity entity = mapper.toEntity(order);

        OrderEntity savedEntity = jpaRepository.save(entity);

        Order savedOrder = mapper.toDomain(savedEntity);

        log.info("Order saved successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public Optional<Order> findById(Long id) {
        log.debug("Finding order by ID: {}", id);

        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        log.debug("Finding orders for customer: {}", customerId);

        return jpaRepository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        log.debug("Finding orders with status: {}", status);

        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        log.debug("Finding all orders");

        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting order with ID: {}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public List<OrderStatusCount> countOrdersByStatus() {
        return jpaRepository.countOrdersByStatus();
    }
}
