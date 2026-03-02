package com.jameson.orderservice.adapter.in.rest;

import com.jameson.orderservice.adapter.in.rest.dto.CreateOrderRequest;
import com.jameson.orderservice.adapter.in.rest.dto.OrderResponse;
import com.jameson.orderservice.adapter.in.rest.dto.OrderStatsResponse;
import com.jameson.orderservice.adapter.in.rest.dto.UpdateOrderStatusRequest;
import com.jameson.orderservice.domain.model.CreateOrderCommand;
import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;
import com.jameson.orderservice.domain.port.in.CreateOrderUseCase;
import com.jameson.orderservice.domain.port.in.GetOrderUseCase;
import com.jameson.orderservice.domain.port.in.UpdateOrderUseCase;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           UpdateOrderUseCase updateOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.updateOrderUseCase = updateOrderUseCase;
    }

    @PostMapping
    @Timed(value = "orders.create", description = "Time taken to create order via REST")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {

        log.info("Received order creation request: {}", request);

        CreateOrderCommand command = new CreateOrderCommand(
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity(),
                request.getTotalAmount()
        );

        Order order = createOrderUseCase.createOrder(command);

        OrderResponse response = OrderResponse.from(order);

        log.info("Order created successfully with ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Timed(value = "orders.get.by.id", description = "Time taken to get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.debug("Fetching order with ID: {}", id);

        return getOrderUseCase.getOrderById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Order not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    @Timed(value = "orders.get.by.customer", description = "Time taken to get orders by customer")
    public ResponseEntity<List<OrderResponse>>  getOrderByCustomer(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) OrderStatus status) {

        List<Order> orders;

        if (customerId != null) {
            log.debug("Fetching orders for customer {} ", customerId);
            orders = getOrderUseCase.getOrdersByCustomerId(customerId);
        } else if (status != null) {
            log.debug("Fetching orders with status {} ", status);
            orders = getOrderUseCase.getOrdersByStatus(status);
        } else {
            log.debug("Fetching all orders");
            orders = getOrderUseCase.getAllOrders();
        }

        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    @Timed(value = "orders.update.status", description = "Time taken to update order status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest statusRequest
            ) {

        Order order = updateOrderUseCase.updateOrderStatus(id, statusRequest.getStatus());

        OrderResponse response = OrderResponse.from(order);

        log.info("Order updated successfully with ID: {}", response.getId());

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @Timed(value = "orders.delete.status", description = "Time taken to delete order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        updateOrderUseCase.deleteOrder(id);
        log.info("Order cancelled successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Timed(value = "orders.stats", description = "Time taken to get order stats")
    public ResponseEntity<OrderStatsResponse> getOrderStats() {
        OrderStatsResponse statsResponse = getOrderUseCase.getOrderStats();
        return ResponseEntity.ok(statsResponse);
    }

}
