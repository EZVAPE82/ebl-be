package com.elfbarlounge.domain.product.api;

import com.elfbarlounge.domain.product.api.dto.ProductResponse;
import com.elfbarlounge.domain.product.api.dto.ProductSummary;
import com.elfbarlounge.domain.product.application.CategoryBrandService;
import com.elfbarlounge.domain.product.application.ProductService;
import com.elfbarlounge.domain.product.domain.Brand;
import com.elfbarlounge.domain.product.domain.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 공개 상품 조회 API (인증 불필요 - SecurityConfig /api/v1/public).
 *
 * 정렬 옵션 (sort 파라미터):
 *  - popular : view_count DESC (기본)
 *  - newest  : id DESC
 *  - price_asc / price_desc
 *  - rating  : rating_avg DESC
 *  - reviews : review_count DESC
 */
@Tag(name = "PublicProduct")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;
    private final CategoryBrandService categoryBrandService;

    @Operation(summary = "상품 목록 (정렬·필터·검색)")
    @GetMapping("/products")
    public Page<ProductSummary> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        size = Math.min(Math.max(size, 1), 60);
        Sort sortBy = resolveSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        return productService.search(keyword, categoryId, brandId, minPrice, maxPrice, true, pageable)
                .map(ProductSummary::from);
    }

    @Operation(summary = "상품 상세 (공개) — 조회수 +1")
    @GetMapping("/products/{id}")
    public ProductResponse detail(@PathVariable Long id) {
        return ProductResponse.from(productService.getPublicDetail(id));
    }

    @Operation(summary = "연관 상품 (같은 카테고리)")
    @GetMapping("/products/{id}/related")
    public Page<ProductSummary> related(@PathVariable Long id,
                                        @RequestParam(defaultValue = "8") int size) {
        size = Math.min(Math.max(size, 1), 24);
        return productService.relatedProducts(id, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "viewCount")))
                .map(ProductSummary::from);
    }

    @Operation(summary = "노출 카테고리 목록")
    @GetMapping("/categories")
    public List<Category> categories() {
        return categoryBrandService.listVisibleCategories();
    }

    @Operation(summary = "브랜드 목록")
    @GetMapping("/brands")
    public List<Brand> brands() {
        return categoryBrandService.listBrands();
    }

    private Sort resolveSort(String sort) {
        return switch (sort) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "id");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "ratingAvg");
            case "reviews" -> Sort.by(Sort.Direction.DESC, "reviewCount");
            default -> Sort.by(Sort.Direction.DESC, "viewCount"); // popular
        };
    }
}
