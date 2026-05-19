package com.elfbarlounge.domain.content.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByVisibleTrueOrderBySortOrderAsc();
    List<Faq> findByCategoryAndVisibleTrueOrderBySortOrderAsc(String category);
}
