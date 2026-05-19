package com.elfbarlounge.domain.product.application;

import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> build(
            String keyword,
            Long categoryId,
            Long brandId,
            Long minPrice,
            Long maxPrice,
            boolean publicOnly
    ) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> ps = new ArrayList<>();
            // soft delete 제외
            ps.add(cb.isNull(root.get("deletedAt")));

            if (publicOnly) {
                // 공개 영역: ACTIVE / SOLD_OUT만 (DRAFT, DISCONTINUED 제외)
                ps.add(root.get("status").in(ProductStatus.ACTIVE, ProductStatus.SOLD_OUT));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("name")), like));
            }
            if (categoryId != null) {
                ps.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (brandId != null) {
                ps.add(cb.equal(root.get("brandId"), brandId));
            }
            if (minPrice != null) {
                ps.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                ps.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(ps.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
