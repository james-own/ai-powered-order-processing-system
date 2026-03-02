package com.jameson.orderservice.domain.port.in;

import com.jameson.orderservice.domain.model.CreateOrderCommand;
import com.jameson.orderservice.domain.model.Order;

/**
 * CreateOrderUseCase - Input Port (Use Case Interface)
 * This defines WHAT the application can do, not HOW.
 * It's a contract that the application layer will implement.
 * In hexagonal architecture, this is an INPUT PORT - an entry point into the domain.
 */
public interface CreateOrderUseCase {

    /**
     * Create a new order.
     *
     * @param command the order creation command
     * @return the created order with generated ID
     */
    Order createOrder(CreateOrderCommand command);
}
