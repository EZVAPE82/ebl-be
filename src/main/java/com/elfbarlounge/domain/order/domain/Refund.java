package com.elfbarlounge.domain.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "refunds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "shipping_fee_deducted", nullable = false)
    private long shippingFeeDeducted;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    private Refund(Long orderId, String reason, long amount, long shippingFeeDeducted) {
        this.orderId = orderId;
        this.reason = reason;
        this.amount = amount;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.status = Status.REQUESTED;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() { this.status = Status.APPROVED; }
    public void reject() { this.status = Status.REJECTED; }
    public void complete() { this.status = Status.COMPLETED; this.completedAt = LocalDateTime.now(); }

    public enum Status { REQUESTED, APPROVED, REJECTED, COMPLETED }
}
