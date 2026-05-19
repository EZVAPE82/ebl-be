package com.elfbarlounge.domain.coupon.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    Page<MemberCoupon> findByMemberIdOrderByExpiresAtAsc(Long memberId, Pageable pageable);

    List<MemberCoupon> findByMemberIdAndUsedAtIsNullAndExpiresAtAfter(Long memberId, LocalDateTime now);

    Optional<MemberCoupon> findByIdAndMemberId(Long id, Long memberId);
}
