package com.jameson.orderservice.domain.service;

import com.jameson.orderservice.adapter.in.rest.dto.OrderStatsResponse;
import com.jameson.orderservice.domain.port.out.OrderStatusCount;
import com.jameson.orderservice.domain.exception.OrderConflictException;
import com.jameson.orderservice.domain.exception.OrderNotFoundException;
import com.jameson.orderservice.domain.model.CreateOrderCommand;
import com.jameson.orderservice.domain.model.FraudScore;
import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;
import com.jameson.orderservice.domain.port.in.CreateOrderUseCase;
import com.jameson.orderservice.domain.port.in.GetOrderUseCase;
import com.jameson.orderservice.domain.port.in.UpdateOrderUseCase;
import com.jameson.orderservice.domain.port.out.OrderRepository;
import com.jameson.orderservice.infrastructure.logging.LoggingContext;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final Counter orderCreatedCounter;
    private final Counter orderCreatedFailedCounter;
    private final Counter orderUpdatedCounter;
    private final Counter orderUpdatedFailedCounter;
    private final Counter orderCancelledCounter;
    private final Timer orderCreationTimer;
    private final Timer orderUpdateTimer;

    public OrderService(OrderRepository orderRepository, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;

        // Register metrics
        this.orderCreatedCounter = Counter.builder("orders.created")
                .description("Total orders created successfully")
                .tag("status", "success")
                .register(meterRegistry);

        this.orderCreatedFailedCounter = Counter.builder("orders.created")
                .description("Total orders creation failed")
                .tag("status", "failed")
                .register(meterRegistry);

        this.orderUpdatedFailedCounter = Counter.builder("orders.updated")
                .description("Total orders updated failed")
                .tag("status", "failed")
                .register(meterRegistry);

        this.orderUpdatedCounter = Counter.builder("orders.updated")
                .description("Total orders updated")
                .tag("operation", "status_change")
                .register(meterRegistry);

        this.orderCancelledCounter = Counter.builder("orders.cancelled")
                .description("Total orders cancelled")
                .register(meterRegistry);

        this.orderCreationTimer = Timer.builder("orders.creation.time")
                .description("Time taken to create an order")
                .register(meterRegistry);

        this.orderUpdateTimer = Timer.builder("orders.update.time")
                .description("Time taken to update an order")
                .register(meterRegistry);

    }

    @Override
    public Order createOrder(CreateOrderCommand command) {

        return orderCreationTimer.record(() -> {
            // Set logging context for this request
            LoggingContext.setCustomerId(command.getCustomerId());
            LoggingContext.setProductId(command.getProductId());

            log.info("Creating order for customer: {}, product: {}", command.getCustomerId(), command.getProductId());

            try {
                // 1. Create domain object (validation happens here)
                Order order = new Order(
                        command.getCustomerId(),
                        command.getProductId(),
                        command.getQuantity(),
                        command.getTotalAmount()
                );

                // 2. Run basic fraud detection (simple rule-based for now)
                FraudScore fraudScore = detectFraud(order);
                order.markAsSuspicious(fraudScore);

                log.debug("Fraud detection complete. Score: {}, Risk: {}", fraudScore.getScore(), fraudScore.getRiskLevel());

                Order savedOrder = orderRepository.save(order);

                // Add order ID to context for subsequent logs
                LoggingContext.setOrderId(savedOrder.getId());

                log.info("Order created successfully with ID: {}, Status: {}",
                        savedOrder.getId(), savedOrder.getStatus());

                // 4. Increment success counter
                orderCreatedCounter.increment();

                // TODO: Publish OrderCreatedEvent to Kafka (next step)

                return savedOrder;

            } catch (Exception e) {
                log.error("Failed to create order for customer: {}", command.getCustomerId(), e);
                orderCreatedFailedCounter.increment();
                throw e;
            } finally {
                LoggingContext.clearOrderContext();
            }
        });

    }

    // ==================== Get Order Use Cases ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        log.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(String customerId) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAll();
    }

    @Override
    public OrderStatsResponse getOrderStats() {
        long count = orderRepository.count();
        List<OrderStatusCount> stats = orderRepository.countOrdersByStatus();

        Map<OrderStatus, Long> byStatus  = new HashMap<>();

        for(OrderStatusCount s : stats) {
            byStatus.put(s.getStatus(), s.getCount());
        }

        return new OrderStatsResponse(count, byStatus);
    }

    // ==================== Update Order Use Cases ====================
    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {

        return orderUpdateTimer.record(() -> {

            LoggingContext.setOrderId(id);
            log.info("Updating status for order: {}", id);

            try {
                Order order = getOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + id));

                switch (status) {
                    case CONFIRMED:
                        order.confirm();
                        break;
                    case PROCESSING:
                        order.process();
                        break;
                    case SHIPPED:
                        order.ship();
                        break;
                    case COMPLETED:
                        order.complete();
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid status: " + status);
                }

                Order savedOrder = orderRepository.save(order);

                log.info("Order updated successfully with ID: {}, Status: {}", savedOrder.getId(), savedOrder.getStatus());

                LoggingContext.setOrderId(savedOrder.getId());

                orderUpdatedCounter.increment();

                return savedOrder;
            } catch (Exception e) {
                log.error("Failed to update order : {}", id, e);
                orderUpdatedFailedCounter.increment();
                throw e;
            } finally {
                LoggingContext.clearOrderContext();
            }
        });

    }

    @Override
    public Order deleteOrder(Long id) {
        return orderUpdateTimer.record(() -> {

            LoggingContext.setOrderId(id);
            log.info("Deleting status for order: {}", id);

            try {
                Order order = getOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + id));

                if (order.canBeCancelled()) {
                    order.cancel();
                } else {
                    throw new OrderConflictException(String.format("Order with status: %s can't be cancelled", order.getStatus()));
                }

                Order updatedOrder = orderRepository.save(order);
                log.info("Order cancelled successfully with ID: {}, Status: {}", updatedOrder.getId(), updatedOrder.getStatus());
                LoggingContext.setOrderId(updatedOrder.getId());
                orderCancelledCounter.increment();

                return updatedOrder;
            } catch (Exception e) {
                log.error("Failed to delete order : {}", id, e);
                orderUpdatedFailedCounter.increment();
                throw e;
            } finally {
                LoggingContext.clearOrderContext();
            }
        });
    }

    /**
     * Simple rule-based fraud detection.
     * TODO: Replace with AI-powered fraud detection (Claude API).
     * For now, uses simple rules:
     * - High value orders (>$1000) = medium risk
     * - Very high value orders (>$5000) = high risk
     * - Everything else = low risk
     */
    private FraudScore detectFraud(Order order) {
        double amount = order.getTotalAmount().doubleValue();

        if (amount > 5000) {
            log.warn("High value order detected: ${}", amount);
            return FraudScore.highRisk("High value transaction");
        } else if (amount > 1000) {
            log.warn("Medium  value order detected: ${}", amount);
            return new FraudScore(0.5, FraudScore.RiskLevel.MEDIUM, "Medium value transaction");
        } else {
            return FraudScore.lowRisk();
        }
    }
}
