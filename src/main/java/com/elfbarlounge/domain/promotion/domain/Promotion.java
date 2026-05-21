package com.elfbarlounge.domain.promotion.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 프로모션 (2+1·10+1 BOGO 등).
 *
 * - type=BOGO_SAME: 동일 상품 N 구매 시 M 증정 (gift_product_id NULL)
 * - type=BOGO_OTHER: 트리거 상품 N 구매 시 다른 사은품 M 증정
 *
 * 트리거 대상 상품은 promotion_products 매핑 테이블 (다대다, 같은 프로모션이 여러 상품에 적용 가능).
 */
@Getter
@Entity
@Table(name = "promotions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private Type type;

    @Column(name = "buy_quantity", nullable = false)
    private int buyQuantity;

    @Column(name = "get_quantity", nullable = false)
    private int getQuantity;

    @Column(name = "gift_product_id")
    private Long giftProductId;

    @Column(name = "gift_product_option_id")
    private Long giftProductOptionId;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "active", nullable = false)
    private boolean active;

    /** promotion_products 매핑 — 이 프로모션이 적용되는 상품 ID 집합 */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "promotion_products", joinColumns = @JoinColumn(name = "promotion_id"))
    @Column(name = "product_id")
    private Set<Long> productIds = new HashSet<>();

    @Builder
    private Promotion(String name, Type type, int buyQuantity, int getQuantity,
                      Long giftProductId, Long giftProductOptionId,
                      LocalDateTime validFrom, LocalDateTime validTo,
                      Boolean active, Set<Long> productIds) {
        this.name = name;
        this.type = type;
        this.buyQuantity = buyQuantity;
        this.getQuantity = getQuantity;
        this.giftProductId = giftProductId;
        this.giftProductOptionId = giftProductOptionId;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.active = active == null || active;
        if (productIds != null) this.productIds.addAll(productIds);
    }

    public boolean isApplicableNow() {
        if (!active) return false;
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(validFrom) && !now.isAfter(validTo);
    }

    /**
     * 구매 수량 N 일 때 증정 수량 계산.
     * 예: 2+1, N=5 → floor(5/2) * 1 = 2개 증정
     */
    public int calcFreeQuantity(int purchased) {
        if (purchased < buyQuantity) return 0;
        return (purchased / buyQuantity) * getQuantity;
    }

    public void deactivate() {
        this.active = false;
    }

    public enum Type {
        /** 동일 상품 증정 (gift_product_id 사용 안 함) */
        BOGO_SAME,
        /** 별도 사은품 증정 (gift_product_id 필수) */
        BOGO_OTHER
    }
}
