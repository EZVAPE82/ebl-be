package com.elfbarlounge.domain.coupon.api.dto;

import com.elfbarlounge.domain.coupon.domain.Coupon;
import com.elfbarlounge.domain.coupon.domain.MemberCoupon;

import java.time.LocalDateTime;

public record CouponView(
        Long memberCouponId,
        Long couponId,
        String name,
        Coupon.CouponType type,
        Coupon.DiscountType discountType,
        long discountValue,
        long minOrderAmount,
        long maxDiscount,
        LocalDateTime expiresAt,
        LocalDateTime usedAt
) {
    public static CouponView of(MemberCoupon mc, Coupon c) {
        return new CouponView(
                mc.getId(), c.getId(), c.getName(), c.getType(), c.getDiscountType(),
                c.getDiscountValue(), c.getMinOrderAmount(), c.getMaxDiscount(),
                mc.getExpiresAt(), mc.getUsedAt()
        );
    }
}
