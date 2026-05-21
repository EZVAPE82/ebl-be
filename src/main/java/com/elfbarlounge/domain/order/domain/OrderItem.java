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

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_option_id")
    private Long productOptionId;

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "option_text", length = 200)
    private String optionText;

    @Column(name = "unit_price", nullable = false)
    private long unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "subtotal", nullable = false)
    private long subtotal;

    /** PAID = 유료 라인 (기본), FREE_GIFT = 프로모션 무료 증정 라인 */
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = 16, nullable = false)
    private Kind kind;

    /** FREE_GIFT 인 경우 어떤 프로모션 적용으로 추가됐는지 추적 (환불·정산용) */
    @Column(name = "source_promotion_id")
    private Long sourcePromotionId;

    @Builder
    public OrderItem(Long productId, Long productOptionId, String productName, String optionText,
                     long unitPrice, int quantity,
                     Kind kind, Long sourcePromotionId) {
        this.productId = productId;
        this.productOptionId = productOptionId;
        this.productName = productName;
        this.optionText = optionText;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice * quantity;
        this.kind = kind != null ? kind : Kind.PAID;
        this.sourcePromotionId = sourcePromotionId;
    }

    public boolean isFreeGift() {
        return kind == Kind.FREE_GIFT;
    }

    public enum Kind { PAID, FREE_GIFT }
}
