package com.elfbarlounge.domain.product.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "slug", length = 200, nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "compatibility_info", columnDefinition = "TEXT")
    private String compatibilityInfo;

    @Column(name = "price", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private ProductStatus status;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "review_count", nullable = false)
    private long reviewCount;

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "stock_threshold", nullable = false)
    private int stockThreshold;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    @OrderBy("sortOrder ASC")
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @Builder
    private Product(Long categoryId, Long brandId, String name, String slug,
                    String description, String compatibilityInfo, Long price,
                    ProductStatus status, String thumbnailUrl, int stockThreshold) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.compatibilityInfo = compatibilityInfo;
        this.price = price;
        this.status = status != null ? status : ProductStatus.DRAFT;
        this.thumbnailUrl = thumbnailUrl;
        this.stockThreshold = stockThreshold;
        this.ratingAvg = BigDecimal.ZERO;
    }

    public void update(Long categoryId, Long brandId, String name, String slug,
                       String description, String compatibilityInfo, Long price,
                       String thumbnailUrl, int stockThreshold) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.compatibilityInfo = compatibilityInfo;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.stockThreshold = stockThreshold;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public void softDelete() {
        this.status = ProductStatus.DISCONTINUED;
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    /** 옵션·이미지 일괄 교체 (어드민 update 시) */
    public void replaceOptions(List<ProductOption> newOptions) {
        this.options.clear();
        if (newOptions != null) {
            this.options.addAll(newOptions);
        }
    }

    public void replaceImages(List<ProductImage> newImages) {
        this.images.clear();
        if (newImages != null) {
            this.images.addAll(newImages);
        }
    }

    public boolean isLowStock() {
        if (stockThreshold <= 0) return false;
        int totalStock = options.stream().mapToInt(ProductOption::getStock).sum();
        return totalStock <= stockThreshold;
    }
}
