package com.elfbarlounge.domain.product.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.product.api.dto.BrandRequest;
import com.elfbarlounge.domain.product.api.dto.CategoryRequest;
import com.elfbarlounge.domain.product.domain.Brand;
import com.elfbarlounge.domain.product.domain.BrandRepository;
import com.elfbarlounge.domain.product.domain.Category;
import com.elfbarlounge.domain.product.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryBrandService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    // ----- Category -----

    @Transactional
    public Long createCategory(CategoryRequest req) {
        if (categoryRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("CATEGORY_SLUG_DUPLICATED", "동일한 slug의 카테고리가 존재합니다.");
        }
        Category c = Category.builder()
                .parentId(req.parentId())
                .name(req.name())
                .slug(req.slug())
                .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                .visible(req.visible() == null || req.visible())
                .build();
        return categoryRepository.save(c).getId();
    }

    @Transactional
    public void updateCategory(Long id, CategoryRequest req) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다."));
        if (!c.getSlug().equals(req.slug()) && categoryRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("CATEGORY_SLUG_DUPLICATED", "동일한 slug의 카테고리가 존재합니다.");
        }
        c.update(req.name(), req.slug(), req.parentId(),
                req.sortOrder() != null ? req.sortOrder() : 0,
                req.visible() == null || req.visible());
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw ApiException.notFound("CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다.");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Category> listAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> listVisibleCategories() {
        return categoryRepository.findAllByVisibleTrueOrderBySortOrderAsc();
    }

    // ----- Brand -----

    @Transactional
    public Long createBrand(BrandRequest req) {
        if (brandRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("BRAND_SLUG_DUPLICATED", "동일한 slug의 브랜드가 존재합니다.");
        }
        Brand b = Brand.builder()
                .name(req.name())
                .slug(req.slug())
                .logoUrl(req.logoUrl())
                .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                .build();
        return brandRepository.save(b).getId();
    }

    @Transactional
    public void updateBrand(Long id, BrandRequest req) {
        Brand b = brandRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("BRAND_NOT_FOUND", "브랜드를 찾을 수 없습니다."));
        if (!b.getSlug().equals(req.slug()) && brandRepository.existsBySlug(req.slug())) {
            throw ApiException.conflict("BRAND_SLUG_DUPLICATED", "동일한 slug의 브랜드가 존재합니다.");
        }
        b.update(req.name(), req.slug(), req.logoUrl(),
                req.sortOrder() != null ? req.sortOrder() : 0);
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw ApiException.notFound("BRAND_NOT_FOUND", "브랜드를 찾을 수 없습니다.");
        }
        brandRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Brand> listBrands() {
        return brandRepository.findAllByOrderBySortOrderAsc();
    }
}
