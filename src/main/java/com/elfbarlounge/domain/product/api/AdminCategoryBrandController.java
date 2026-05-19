package com.elfbarlounge.domain.product.api;

import com.elfbarlounge.domain.product.api.dto.BrandRequest;
import com.elfbarlounge.domain.product.api.dto.CategoryRequest;
import com.elfbarlounge.domain.product.application.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "AdminCategoryBrand")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCategoryBrandController {

    private final CategoryBrandService service;

    @Operation(summary = "[Admin] 카테고리 등록")
    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody CategoryRequest req) {
        Long id = service.createCategory(req);
        return ResponseEntity.created(URI.create("/api/v1/admin/categories/" + id))
                .body(Map.of("id", id));
    }

    @Operation(summary = "[Admin] 카테고리 수정")
    @PutMapping("/categories/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        service.updateCategory(id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 카테고리 삭제")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 브랜드 등록")
    @PostMapping("/brands")
    public ResponseEntity<Map<String, Object>> createBrand(@Valid @RequestBody BrandRequest req) {
        Long id = service.createBrand(req);
        return ResponseEntity.created(URI.create("/api/v1/admin/brands/" + id))
                .body(Map.of("id", id));
    }

    @Operation(summary = "[Admin] 브랜드 수정")
    @PutMapping("/brands/{id}")
    public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest req) {
        service.updateBrand(id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 브랜드 삭제")
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        service.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
