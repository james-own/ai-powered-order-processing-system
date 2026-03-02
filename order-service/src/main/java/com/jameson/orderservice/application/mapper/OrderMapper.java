package com.jameson.orderservice.application.mapper;

import com.jameson.orderservice.adapter.out.persistence.OrderEntity;
import com.jameson.orderservice.domain.model.FraudScore;
import com.jameson.orderservice.domain.model.Order;
import org.springframework.stereotype.Component;

/**
 * OrderMapper - Handles conversion between Domain and Persistence layers
 * This mapper is part of the APPLICATION layer and helps with the transformation
 * between domain models and infrastructure entities.
 * Alternative: Could use MapStruct for automatic mapping, but manual mapping
 * gives us more control and is more explicit.
 */
@Component
public class OrderMapper {


    public Order toDomain(OrderEntity entity) {
       if (entity == null) {
            return null;
        }

        FraudScore fraudScore = null;

       if (entity.getFraudScore() != null && entity.getFraudRiskLevel() != null) {
            FraudScore.RiskLevel riskLevel = FraudScore.RiskLevel.valueOf(entity.getFraudRiskLevel());
            fraudScore = new FraudScore(entity.getFraudScore(), riskLevel, entity.getFraudReason());
       }

       return Order.reconstruct(
               entity.getId(),
               entity.getCustomerId(),
               entity.getProductId(),
               entity.getQuantity(),
               entity.getTotalAmount(),
               entity.getStatus(),
               fraudScore,
               entity.getCreatedAt(),
               entity.getUpdatedAt()
       );
    }

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setProductId(order.getProductId());
        entity.setQuantity(order.getQuantity());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus());

        if (order.getFraudScore() != null) {
            entity.setFraudScore(order.getFraudScore().getScore());
            entity.setFraudRiskLevel(order.getFraudScore().getRiskLevel().name());
            entity.setFraudReason(order.getFraudScore().getReason());
        }

        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());

        return entity;
    }
}
