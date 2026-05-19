package com.elfbarlounge.domain.content.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByPlacementAndVisibleTrueOrderBySortOrderAsc(Banner.Placement placement);
}
