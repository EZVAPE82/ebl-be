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

/**
 * 결제 거래 로그.
 * 보안 룰 #1: 카드번호·CVC·유효기간 등 PCI 민감정보 컬럼 자체를 두지 않음.
 * raw_response: PG 응답 원본을 저장하되 민감 필드는 마스킹한 형태로만 저장 권장.
 */
@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "pg_provider", length = 20, nullable = false)
    private String pgProvider;

    @Column(name = "pg_tx_id", length = 64)
    private String pgTxId;

    @Column(name = "method", length = 20, nullable = false)
    private String method;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Payment(Long orderId, String pgProvider, String pgTxId, String method,
                    long amount, Status status, String rawResponse) {
        this.orderId = orderId;
        this.pgProvider = pgProvider;
        this.pgTxId = pgTxId;
        this.method = method;
        this.amount = amount;
        this.status = status != null ? status : Status.READY;
        this.rawResponse = rawResponse;
        this.createdAt = LocalDateTime.now();
    }

    public void markPaid(String pgTxId, String rawResponse) {
        this.status = Status.PAID;
        this.pgTxId = pgTxId;
        this.approvedAt = LocalDateTime.now();
        this.rawResponse = rawResponse;
    }

    public void markCanceled(String rawResponse) {
        this.status = Status.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.rawResponse = rawResponse;
    }

    public void markRefunded() {
        this.status = Status.REFUNDED;
        this.canceledAt = LocalDateTime.now();
    }

    public enum Status { READY, PAID, FAILED, CANCELED, PARTIAL_REFUND, REFUNDED }
}
