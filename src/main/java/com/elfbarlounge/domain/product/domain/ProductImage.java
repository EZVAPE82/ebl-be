package com.elfbarlounge.domain.product.domain;

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
@Table(name = "product_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "url", columnDefinition = "TEXT", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private Type type;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    private ProductImage(String url, Type type, int sortOrder) {
        this.url = url;
        this.type = type;
        this.sortOrder = sortOrder;
    }

    public enum Type {
        THUMBNAIL, DETAIL
    }
}
