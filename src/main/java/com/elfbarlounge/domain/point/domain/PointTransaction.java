package com.elfbarlounge.domain.point.domain;

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
 * 적립금 원장. 잔액은 항상 sum(amount) 또는 마지막 row의 balance_after.
 * 절대 row를 update하지 않음 — append-only.
 */
@Getter
@Entity
@Table(name = "point_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private Type type;

    /** + or - (USE/EXPIRE 음수, EARN/REFUND 양수, ADMIN_ADJUST 양/음) */
    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "source_type", length = 20)
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private PointTransaction(Long memberId, Type type, long amount, long balanceAfter,
                             String sourceType, Long sourceId, LocalDateTime expiresAt, String memo) {
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.expiresAt = expiresAt;
        this.memo = memo;
        this.createdAt = LocalDateTime.now();
    }

    public enum Type {
        EARN, USE, EXPIRE, REFUND, ADMIN_ADJUST
    }
}
