package com.elfbarlounge.domain.product.api.dto;

import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductStatus;

import java.math.BigDecimal;

/**
 * 상품 목록용 슬림한 DTO.
 */
public record ProductSummary(
        Long id,
        Long categoryId,
        Long brandId,
        String name,
        String slug,
        Long price,
        ProductStatus status,
        String thumbnailUrl,
        long reviewCount,
        BigDecimal ratingAvg
) {
    public static ProductSummary from(Product p) {
        return new ProductSummary(
                p.getId(), p.getCategoryId(), p.getBrandId(),
                p.getName(), p.getSlug(), p.getPrice(), p.getStatus(),
                p.getThumbnailUrl(), p.getReviewCount(), p.getRatingAvg()
        );
    }
}
