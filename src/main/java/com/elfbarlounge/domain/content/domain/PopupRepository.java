package com.elfbarlounge.domain.content.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    List<Popup> findByVisibleTrueOrderBySortOrderAsc();
}
