package com.jameson.orderservice.domain.model;

/**
 * OrderStatus - Order lifecycle states
 * Represents all possible states an order can be in.
 */
public enum OrderStatus {

    /**
     * Order created, awaiting fraud check and inventory validation
     */
    PENDING,

    /**
     * Order flagged for manual review due to high fraud risk
     */
    PENDING_REVIEW,

    /**
     * Order needs additional verification (medium fraud risk)
     */
    PENDING_VERIFICATION,

    /**
     * Order confirmed, inventory reserved
     */
    CONFIRMED,

    /**
     * Order is being prepared for shipment
     */
    PROCESSING,

    /**
     * Order has been shipped
     */
    SHIPPED,

    /**
     * Order delivered and completed
     */
    COMPLETED,

    /**
     * Order cancelled by customer or system
     */
    CANCELLED,

    /**
     * Order rejected (insufficient inventory, fraud, etc.)
     */
    REJECTED;

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }

    public boolean isActive() {
        return this == PENDING
                || this == PENDING_REVIEW
                || this == PENDING_VERIFICATION
                || this == CONFIRMED
                || this == PROCESSING;
    }
}
