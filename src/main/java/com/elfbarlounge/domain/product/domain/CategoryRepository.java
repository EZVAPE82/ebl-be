package com.elfbarlounge.domain.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsBySlug(String slug);
    List<Category> findAllByVisibleTrueOrderBySortOrderAsc();
}
