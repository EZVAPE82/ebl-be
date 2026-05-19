package com.elfbarlounge.domain.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderItemId(Long orderItemId);

    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
