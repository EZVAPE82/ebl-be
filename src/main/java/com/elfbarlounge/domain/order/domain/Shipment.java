package com.elfbarlounge.domain.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "shipments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "courier", length = 40)
    private String courier;

    @Column(name = "tracking_no", length = 64)
    private String trackingNo;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Shipment(Long orderId, String courier, String trackingNo) {
        this.orderId = orderId;
        this.courier = courier;
        this.trackingNo = trackingNo;
        this.createdAt = LocalDateTime.now();
    }

    public void ship(String courier, String trackingNo) {
        this.courier = courier;
        this.trackingNo = trackingNo;
        this.shippedAt = LocalDateTime.now();
    }

    public void markDelivered() {
        this.deliveredAt = LocalDateTime.now();
    }
}
