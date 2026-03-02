package com.jameson.orderservice.adapter.in.rest.dto;

import com.jameson.orderservice.domain.model.OrderStatus;

public class UpdateOrderStatusRequest {

    private OrderStatus status;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
