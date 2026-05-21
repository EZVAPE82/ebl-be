package com.elfbarlounge.domain.promotion.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /** 특정 상품에 적용 가능한 활성 프로모션. PromotionEvaluator 가 결제 직전 호출. */
    @Query("""
        SELECT DISTINCT p FROM Promotion p
        JOIN p.productIds pid
        WHERE pid = :productId
          AND p.active = true
          AND p.validFrom <= :now
          AND p.validTo >= :now
    """)
    List<Promotion> findActiveByProductId(@Param("productId") Long productId, @Param("now") LocalDateTime now);

    Page<Promotion> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
