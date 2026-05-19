package com.elfbarlounge.domain.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByMemberIdAndProductId(Long memberId, Long productId);
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
    Page<WishlistItem> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    long countByMemberId(Long memberId);
}
