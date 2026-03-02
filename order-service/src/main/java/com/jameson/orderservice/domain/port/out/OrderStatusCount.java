package com.jameson.orderservice.domain.port.out;

import com.jameson.orderservice.domain.model.OrderStatus;

public interface OrderStatusCount {
    OrderStatus getStatus();

    long getCount();
}