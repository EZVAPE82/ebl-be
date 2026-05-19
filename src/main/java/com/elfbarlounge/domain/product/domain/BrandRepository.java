package com.elfbarlounge.domain.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsBySlug(String slug);
    List<Brand> findAllByOrderBySortOrderAsc();
}
