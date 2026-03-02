package com.jameson.orderservice.infrastructure.logging;

import org.slf4j.MDC;

/**
 * LoggingContext - Utility for adding contextual information to logs
 * Uses SLF4J's MDC (Mapped Diagnostic Context) to add custom fields to logs.
 * These fields will appear in JSON logs and can be queried in Loki/Grafana.
 * Example usage:
 * <pre>
 * LoggingContext.setCustomerId("customer-123");
 * log.info("Processing order");  // Will include customerId in JSON log
 * LoggingContext.clear();
 * </pre>
 */
public class LoggingContext {

    private static final String CUSTOMER_ID = "customerId";
    private static final String ORDER_ID = "orderId";
    private static final String PRODUCT_ID = "productId";
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    // ==================== Customer Context ====================

    /**
     * Add customer ID to logging context.
     */
    public static void setCustomerId(String customerId) {
        if (customerId != null && !customerId.trim().isEmpty()) {
            MDC.put(CUSTOMER_ID, customerId);
        }
    }

    /**
     * Get customer ID from logging context.
     */
    public static String getCustomerId() {
        return MDC.get(CUSTOMER_ID);
    }

    /**
     * Remove customer ID from logging context.
     */
    public static void clearCustomerId() {
        MDC.remove(CUSTOMER_ID);
    }

    // ==================== Order Context ====================

    /**
     * Add order ID to logging context.
     */
    public static void setOrderId(Long orderId) {
        if (orderId != null) {
            MDC.put(ORDER_ID, orderId.toString());
        }
    }

    /**
     * Get order ID from logging context.
     */
    public static String getOrderId() {
        return MDC.get(ORDER_ID);
    }

    /**
     * Remove order ID from logging context.
     */
    public static void clearOrderId() {
        MDC.remove(ORDER_ID);
    }

    // ==================== Product Context ====================

    /**
     * Add product ID to logging context.
     */
    public static void setProductId(String productId) {
        if (productId != null && !productId.trim().isEmpty()) {
            MDC.put(PRODUCT_ID, productId);
        }
    }

    /**
     * Get product ID from logging context.
     */
    public static String getProductId() {
        return MDC.get(PRODUCT_ID);
    }

    /**
     * Remove product ID from logging context.
     */
    public static void clearProductId() {
        MDC.remove(PRODUCT_ID);
    }

    // ==================== Tracing Context ====================

    /**
     * Add trace ID to logging context (for distributed tracing).
     */
    public static void setTraceId(String traceId) {
        if (traceId != null && !traceId.trim().isEmpty()) {
            MDC.put(TRACE_ID, traceId);
        }
    }

    /**
     * Get trace ID from logging context.
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * Add span ID to logging context (for distributed tracing).
     */
    public static void setSpanId(String spanId) {
        if (spanId != null && !spanId.trim().isEmpty()) {
            MDC.put(SPAN_ID, spanId);
        }
    }

    /**
     * Get span ID from logging context.
     */
    public static String getSpanId() {
        return MDC.get(SPAN_ID);
    }

    // ==================== Bulk Operations ====================

    /**
     * Set order context (customer + order + product).
     */
    public static void setOrderContext(String customerId, Long orderId, String productId) {
        setCustomerId(customerId);
        setOrderId(orderId);
        setProductId(productId);
    }

    /**
     * Clear all context.
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Clear order-specific context.
     */
    public static void clearOrderContext() {
        clearCustomerId();
        clearOrderId();
        clearProductId();
    }

    // ==================== Try-with-resources Support ====================

    /**
     * Create a scoped context that auto-clears when closed.
     * Example usage:
     * <pre>
     * try (var context = LoggingContext.withCustomerId("customer-123")) {
     *     log.info("Processing...");  // Has customerId in logs
     * } // Auto-cleared here
     * </pre>
     */
    public static ScopedContext withCustomerId(String customerId) {
        setCustomerId(customerId);
        return new ScopedContext();
    }

    /**
     * Create a scoped order context.
     */
    public static ScopedContext withOrderContext(String customerId, Long orderId, String productId) {
        setOrderContext(customerId, orderId, productId);
        return new ScopedContext();
    }

    /**
     * Auto-closeable context for try-with-resources.
     */
    public static class ScopedContext implements AutoCloseable {
        @Override
        public void close() {
            clear();
        }
    }
}
