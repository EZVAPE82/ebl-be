package com.elfbarlounge.domain.order.domain;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PREPARING,
    SHIPPING,
    DELIVERED,
    CANCELED,
    REFUNDED
}
