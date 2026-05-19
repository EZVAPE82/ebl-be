package com.elfbarlounge.domain.coupon.api.dto;

import com.elfbarlounge.domain.coupon.domain.Coupon;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CouponRequest(
        @Size(max = 40) String code,
        @NotBlank @Size(max = 80) String name,
        @NotNull Coupon.CouponType type,
        @NotNull Coupon.DiscountType discountType,
        @PositiveOrZero long discountValue,
        @PositiveOrZero long minOrderAmount,
        @PositiveOrZero long maxDiscount,
        @Min(1) int validDays,
        Boolean active
) {}
