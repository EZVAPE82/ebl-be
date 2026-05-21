package com.elfbarlounge.domain.qna.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQnaRepository extends JpaRepository<ProductQna, Long> {

    Page<ProductQna> findByProductIdAndVisibleTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<ProductQna> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
