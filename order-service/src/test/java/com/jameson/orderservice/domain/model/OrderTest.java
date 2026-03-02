package com.jameson.orderservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    public final String CUSTOMER_ID = "customer-123";
    public final  String PRODUCT_ID= "product-456";

    @Test
    void shouldCreateOrderWithValidData() {
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");

        Order order = new Order(CUSTOMER_ID, PRODUCT_ID, quantity, totalAmount);

        assertNotNull(order);
        assertEquals(CUSTOMER_ID, order.getCustomerId());
        assertEquals(PRODUCT_ID, order.getProductId());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getFraudScore());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("99.99");

        assertThrows(IllegalArgumentException.class, ()-> {
            new Order(null, PRODUCT_ID, quantity, totalAmount);
        });

    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        Integer quantity = 0;
        BigDecimal totalAmount = new BigDecimal("99.99");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Order(CUSTOMER_ID, PRODUCT_ID, quantity, totalAmount)
        );

        assertTrue(exception.getMessage().contains("greater than 0"));
    }

    @Test
    void shouldThrowExceptionWhenQuantityExceeds100() {
        Integer quantity = 101;
        BigDecimal totalAmount = new BigDecimal("99.99");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Order(CUSTOMER_ID, PRODUCT_ID, quantity, totalAmount)
        );

        assertTrue(exception.getMessage().contains("cannot exceed 100"));
    }

    @Test
    void shouldMarkOrderAsSuspiciousWithHighRiskFraudScore() {
        Order order = createValidOrder();
        FraudScore highRiskScore = new FraudScore(0.85, FraudScore.RiskLevel.HIGH, "Suspicious pattern");

        order.markAsSuspicious(highRiskScore);

        assertEquals(OrderStatus.PENDING_REVIEW, order.getStatus());
        assertEquals(highRiskScore, order.getFraudScore());
    }

    @Test
    void shouldMarkOrderForVerificationWithMediumRiskScore() {
        Order order = createValidOrder();
        FraudScore mediumRiskScore = new FraudScore(0.5, FraudScore.RiskLevel.MEDIUM, "Medium risk");

        order.markAsSuspicious(mediumRiskScore);
        assertEquals(OrderStatus.PENDING_VERIFICATION, order.getStatus());
    }

    @Test
    void shouldKeepPendingStatusWithLowRiskScore() {
        Order order = createValidOrder();
        FraudScore lowRiskScore = FraudScore.lowRisk();

        order.markAsSuspicious(lowRiskScore);

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldConfirmPendingOrder() {
        Order order = createValidOrder();
        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingNonPendingOrder() {
        Order order = createValidOrder();
        order.confirm();

        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    void shouldRejectOrderWithReason() {
        Order order = createValidOrder();
        String reason = "Insufficient inventory";

        order.reject(reason);

        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingWithoutReason() {
        Order order = createValidOrder();

        assertThrows(IllegalArgumentException.class, () -> {
            order.reject(null);
        });
    }

    @Test
    void shouldCancelPendingOrder() {
        Order order = createValidOrder();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldNotCancelShippedOrder() {
        Order order = createValidOrder();
        order.confirm();
        order.process();
        order.ship();

        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    void shouldProcessConfirmedOrder() {
        Order order = createValidOrder();
        order.confirm();

        order.process();

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void shouldShipProcessingOrder() {
        Order order = createValidOrder();
        order.confirm();
        order.process();

        order.ship();

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void shouldCompleteShippedOrder() {
        Order order = createValidOrder();
        order.confirm();
        order.process();
        order.ship();

        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void shouldIdentifyFinalStates() {

        Order completedOrder = createValidOrder();
        completedOrder.confirm();
        completedOrder.process();
        completedOrder.ship();
        completedOrder.complete();

        Order cancelledOrder = createValidOrder();
        cancelledOrder.cancel();

        Order rejectedOrder = createValidOrder();
        rejectedOrder.reject("Test");

        Order pendingOrder = createValidOrder();

        assertTrue(completedOrder.isFinalState());
        assertTrue(cancelledOrder.isFinalState());
        assertTrue(rejectedOrder.isFinalState());
        assertFalse(pendingOrder.isFinalState());
    }

    @Test
    void shouldDetermineIfOrderCanBeCancelled() {
        // Given
        Order pendingOrder = createValidOrder();
        Order confirmedOrder = createValidOrder();
        confirmedOrder.confirm();

        Order shippedOrder = createValidOrder();
        shippedOrder.confirm();
        shippedOrder.process();
        shippedOrder.ship();

        assertTrue(pendingOrder.canBeCancelled());
        assertTrue(confirmedOrder.canBeCancelled());
        assertFalse(shippedOrder.canBeCancelled());
    }

    private Order createValidOrder() {
        return new Order(
                "customer-123",
                "product-456",
                2,
                new BigDecimal("99.99")
        );
    }
}