package com.elfbarlounge.domain.product.api.dto;

import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductImage;
import com.elfbarlounge.domain.product.domain.ProductOption;
import com.elfbarlounge.domain.product.domain.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        Long categoryId,
        Long brandId,
        String name,
        String slug,
        String description,
        String compatibilityInfo,
        Long price,
        ProductStatus status,
        String thumbnailUrl,
        long reviewCount,
        BigDecimal ratingAvg,
        List<OptionView> options,
        List<ImageView> images
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getCategoryId(),
                p.getBrandId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getCompatibilityInfo(),
                p.getPrice(),
                p.getStatus(),
                p.getThumbnailUrl(),
                p.getReviewCount(),
                p.getRatingAvg(),
                p.getOptions().stream().map(OptionView::from).toList(),
                p.getImages().stream().map(ImageView::from).toList()
        );
    }

    public record OptionView(
            Long id, String optionGroup, String optionValue,
            long priceDelta, int stock, boolean required, int sortOrder, boolean visible
    ) {
        static OptionView from(ProductOption o) {
            return new OptionView(o.getId(), o.getOptionGroup(), o.getOptionValue(),
                    o.getPriceDelta(), o.getStock(), o.isRequired(), o.getSortOrder(), o.isVisible());
        }
    }

    public record ImageView(Long id, String url, ProductImage.Type type, int sortOrder) {
        static ImageView from(ProductImage i) {
            return new ImageView(i.getId(), i.getUrl(), i.getType(), i.getSortOrder());
        }
    }
}
