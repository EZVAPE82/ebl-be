package com.elfbarlounge.domain.coupon.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByTypeAndActiveTrue(Coupon.CouponType type);
}
