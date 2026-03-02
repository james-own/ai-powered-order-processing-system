package com.jameson.orderservice.adapter.in.rest.dto;

import com.jameson.orderservice.domain.model.OrderStatus;

import java.util.Map;

public class OrderStatsResponse {

    private long totalOrders;
    private Map<OrderStatus, Long> byStatus;

    public OrderStatsResponse() {
    }

    public OrderStatsResponse(long totalOrders, Map<OrderStatus, Long> byStatus) {
        this.totalOrders = totalOrders;
        this.byStatus = byStatus;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public Map<OrderStatus, Long> getByStatus() {
        return byStatus;
    }
}
