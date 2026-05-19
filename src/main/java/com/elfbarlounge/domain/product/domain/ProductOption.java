package com.elfbarlounge.domain.product.domain;

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

@Getter
@Entity
@Table(name = "product_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "option_group", length = 40, nullable = false)
    private String optionGroup;

    @Column(name = "option_value", length = 80, nullable = false)
    private String optionValue;

    @Column(name = "price_delta", nullable = false)
    private long priceDelta;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Builder
    private ProductOption(String optionGroup, String optionValue, long priceDelta,
                          int stock, boolean required, int sortOrder, boolean visible) {
        this.optionGroup = optionGroup;
        this.optionValue = optionValue;
        this.priceDelta = priceDelta;
        this.stock = stock;
        this.required = required;
        this.sortOrder = sortOrder;
        this.visible = visible;
    }

    public void decreaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be positive");
        }
        if (this.stock < qty) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= qty;
    }

    public void increaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be positive");
        }
        this.stock += qty;
    }
}
