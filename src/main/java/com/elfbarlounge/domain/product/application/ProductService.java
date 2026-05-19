package com.elfbarlounge.domain.product.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.product.api.dto.ProductUpsertRequest;
import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductImage;
import com.elfbarlounge.domain.product.domain.ProductOption;
import com.elfbarlounge.domain.product.domain.ProductRepository;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long create(ProductUpsertRequest req) {
        if (productRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("SLUG_DUPLICATED", "동일한 slug의 상품이 이미 존재합니다.");
        }

        Product p = Product.builder()
                .categoryId(req.categoryId())
                .brandId(req.brandId())
                .name(req.name())
                .slug(req.slug())
                .description(req.description())
                .compatibilityInfo(req.compatibilityInfo())
                .price(req.price())
                .status(req.status() != null ? req.status() : ProductStatus.DRAFT)
                .thumbnailUrl(req.thumbnailUrl())
                .stockThreshold(req.stockThreshold() != null ? req.stockThreshold() : 0)
                .build();

        p.replaceOptions(toOptions(req.options()));
        p.replaceImages(toImages(req.images()));

        return productRepository.save(p).getId();
    }

    @Transactional
    public void update(Long id, ProductUpsertRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));

        if (!p.getSlug().equals(req.slug()) && productRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("SLUG_DUPLICATED", "동일한 slug의 상품이 이미 존재합니다.");
        }

        p.update(req.categoryId(), req.brandId(), req.name(), req.slug(),
                req.description(), req.compatibilityInfo(), req.price(),
                req.thumbnailUrl(),
                req.stockThreshold() != null ? req.stockThreshold() : 0);
        if (req.status() != null) {
            p.changeStatus(req.status());
        }
        p.replaceOptions(toOptions(req.options()));
        p.replaceImages(toImages(req.images()));
    }

    @Transactional
    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        p.softDelete();
    }

    @Transactional
    public void changeStatus(Long id, ProductStatus status) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        p.changeStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<Product> search(String keyword, Long categoryId, Long brandId,
                                Long minPrice, Long maxPrice, boolean publicOnly,
                                Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.build(keyword, categoryId, brandId, minPrice, maxPrice, publicOnly);
        return productRepository.findAll(spec, pageable);
    }

    @Transactional
    public Product getPublicDetail(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        if (p.getDeletedAt() != null
                || p.getStatus() == ProductStatus.DRAFT
                || p.getStatus() == ProductStatus.DISCONTINUED) {
            throw ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다.");
        }
        p.increaseViewCount();
        // 트랜잭션 내에서 LAZY 컬렉션 초기화 (open-in-view: false)
        p.getOptions().size();
        p.getImages().size();
        return p;
    }

    @Transactional(readOnly = true)
    public Product getAdminDetail(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        p.getOptions().size();
        p.getImages().size();
        return p;
    }

    @Transactional(readOnly = true)
    public Page<Product> relatedProducts(Long productId, Pageable pageable) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        if (p.getCategoryId() == null) {
            return Page.empty(pageable);
        }
        return productRepository.findByStatusAndCategoryId(ProductStatus.ACTIVE, p.getCategoryId(), pageable);
    }

    private List<ProductOption> toOptions(List<ProductUpsertRequest.OptionInput> in) {
        if (in == null) return List.of();
        return in.stream().map(o -> ProductOption.builder()
                .optionGroup(o.optionGroup())
                .optionValue(o.optionValue())
                .priceDelta(o.priceDelta())
                .stock(o.stock() != null ? o.stock() : 0)
                .required(o.required() == null || o.required())
                .sortOrder(o.sortOrder() != null ? o.sortOrder() : 0)
                .visible(o.visible() == null || o.visible())
                .build()).toList();
    }

    private List<ProductImage> toImages(List<ProductUpsertRequest.ImageInput> in) {
        if (in == null) return List.of();
        return in.stream().map(i -> ProductImage.builder()
                .url(i.url())
                .type(i.type())
                .sortOrder(i.sortOrder() != null ? i.sortOrder() : 0)
                .build()).toList();
    }
}
