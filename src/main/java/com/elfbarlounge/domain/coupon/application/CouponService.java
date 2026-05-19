package com.elfbarlounge.domain.coupon.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.coupon.domain.Coupon;
import com.elfbarlounge.domain.coupon.domain.CouponRepository;
import com.elfbarlounge.domain.coupon.domain.MemberCoupon;
import com.elfbarlounge.domain.coupon.domain.MemberCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    /** 자동 발급: signup 시 SIGNUP, 생일 배치 시 BIRTHDAY 등 type 기반. */
    @Transactional
    public MemberCoupon issueByType(Long memberId, Coupon.CouponType type) {
        return couponRepository.findByTypeAndActiveTrue(type)
                .map(c -> memberCouponRepository.save(
                        MemberCoupon.builder()
                                .memberId(memberId)
                                .couponId(c.getId())
                                .expiresAt(LocalDateTime.now().plusDays(c.getValidDays()))
                                .build()))
                .orElse(null);
    }

    @Transactional
    public MemberCoupon issueDirectly(Long memberId, Long couponId) {
        Coupon c = couponRepository.findById(couponId)
                .orElseThrow(() -> ApiException.notFound("COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."));
        if (!c.isActive()) {
            throw ApiException.badRequest("COUPON_INACTIVE", "비활성 쿠폰입니다.");
        }
        return memberCouponRepository.save(MemberCoupon.builder()
                .memberId(memberId)
                .couponId(couponId)
                .expiresAt(LocalDateTime.now().plusDays(c.getValidDays()))
                .build());
    }

    @Transactional(readOnly = true)
    public List<MemberCoupon> listUsable(Long memberId) {
        return memberCouponRepository.findByMemberIdAndUsedAtIsNullAndExpiresAtAfter(memberId, LocalDateTime.now());
    }

    /**
     * 주문 결제 시 쿠폰 사용 (서비스 외부에서 transactional 컨텍스트로 호출).
     */
    @Transactional
    public long useCoupon(Long memberId, Long memberCouponId, long orderAmount, Long orderId) {
        MemberCoupon mc = memberCouponRepository.findByIdAndMemberId(memberCouponId, memberId)
                .orElseThrow(() -> ApiException.notFound("MEMBER_COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."));
        if (!mc.isUsable()) {
            throw ApiException.badRequest("COUPON_UNUSABLE", "사용할 수 없는 쿠폰입니다.");
        }
        Coupon c = couponRepository.findById(mc.getCouponId())
                .orElseThrow(() -> ApiException.notFound("COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."));
        long discount = c.calcDiscount(orderAmount);
        if (discount <= 0) {
            throw ApiException.badRequest("COUPON_MIN_NOT_MET", "쿠폰 사용 조건을 충족하지 않습니다.");
        }
        mc.use(orderId);
        return discount;
    }

    @Transactional
    public void revertCoupon(Long memberCouponId) {
        memberCouponRepository.findById(memberCouponId).ifPresent(MemberCoupon::revert);
    }
}
