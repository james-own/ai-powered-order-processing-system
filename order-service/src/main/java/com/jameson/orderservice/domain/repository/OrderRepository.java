package com.jameson.orderservice.domain.repository;

import com.jameson.orderservice.domain.model.Order;

public interface OrderRepository {

    Order save(Order order);
}