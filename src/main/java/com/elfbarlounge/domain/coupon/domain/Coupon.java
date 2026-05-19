package com.elfbarlounge.domain.coupon.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 40, unique = true)
    private String code;

    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private CouponType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", length = 16, nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private long discountValue;

    @Column(name = "min_order_amount", nullable = false)
    private long minOrderAmount;

    @Column(name = "max_discount", nullable = false)
    private long maxDiscount;

    @Column(name = "valid_days", nullable = false)
    private int validDays;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Builder
    private Coupon(String code, String name, CouponType type, DiscountType discountType,
                   long discountValue, long minOrderAmount, long maxDiscount, int validDays, Boolean active) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscount = maxDiscount;
        this.validDays = validDays > 0 ? validDays : 30;
        this.active = active == null || active;
    }

    public long calcDiscount(long orderAmount) {
        if (orderAmount < minOrderAmount) {
            return 0;
        }
        long calc = switch (discountType) {
            case AMOUNT -> discountValue;
            case PERCENT -> orderAmount * discountValue / 100;
        };
        if (maxDiscount > 0) {
            calc = Math.min(calc, maxDiscount);
        }
        return Math.min(calc, orderAmount);
    }

    public enum CouponType { SIGNUP, BIRTHDAY, REFERRAL, MANUAL }
    public enum DiscountType { AMOUNT, PERCENT }
}
