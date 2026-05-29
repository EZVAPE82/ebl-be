package com.elfbarlounge.domain.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByOrderItemId(Long orderItemId);

    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    /**
     * 베스트 리뷰 — 별점 4점 이상 + 사진 있는 것 우선, 별점 desc → 최신순.
     * /reviews/best 페이지 fallback (별점 기준 best). 향후 likeCount 추가 시 그걸로 정렬.
     */
    @Query("""
        SELECT r FROM Review r
        WHERE r.rating >= 4
        ORDER BY
          CASE WHEN SIZE(r.photos) > 0 THEN 0 ELSE 1 END ASC,
          r.rating DESC,
          r.createdAt DESC
    """)
    Page<Review> findBestPublic(Pageable pageable);
}
