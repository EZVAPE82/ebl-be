package com.elfbarlounge.domain.product.api;

import com.elfbarlounge.domain.product.api.dto.ProductResponse;
import com.elfbarlounge.domain.product.api.dto.ProductSummary;
import com.elfbarlounge.domain.product.api.dto.ProductUpsertRequest;
import com.elfbarlounge.domain.product.application.ProductService;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "AdminProduct")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "[Admin] 상품 등록")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductUpsertRequest req) {
        Long id = productService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/admin/products/" + id))
                .body(Map.of("id", id));
    }

    @Operation(summary = "[Admin] 상품 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest req) {
        productService.update(id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 상품 삭제 (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 상품 상태 변경")
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam ProductStatus status) {
        productService.changeStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 상품 단건 조회 (모든 상태)")
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return ProductResponse.from(productService.getAdminDetail(id));
    }

    @Operation(summary = "[Admin] 상품 목록 (검색·필터)")
    @GetMapping
    public Page<ProductSummary> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            Pageable pageable
    ) {
        return productService.search(keyword, categoryId, brandId, minPrice, maxPrice, false, pageable)
                .map(ProductSummary::from);
    }
}
