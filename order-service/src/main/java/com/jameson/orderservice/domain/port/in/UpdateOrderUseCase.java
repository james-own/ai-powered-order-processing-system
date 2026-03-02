package com.jameson.orderservice.domain.port.in;

import com.jameson.orderservice.domain.model.Order;
import com.jameson.orderservice.domain.model.OrderStatus;

public interface UpdateOrderUseCase {

    Order updateOrderStatus(Long id, OrderStatus status);
    Order deleteOrder(Long id);
}
