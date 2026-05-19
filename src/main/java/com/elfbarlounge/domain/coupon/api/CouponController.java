package com.elfbarlounge.domain.coupon.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.coupon.api.dto.CouponView;
import com.elfbarlounge.domain.coupon.domain.Coupon;
import com.elfbarlounge.domain.coupon.domain.CouponRepository;
import com.elfbarlounge.domain.coupon.domain.MemberCoupon;
import com.elfbarlounge.domain.coupon.domain.MemberCouponRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Coupon")
@RestController
@RequestMapping("/api/v1/members/me/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;

    @Operation(summary = "내 쿠폰함")
    @GetMapping
    public Page<CouponView> myCoupons(@AuthenticationPrincipal AuthPrincipal principal, Pageable pageable) {
        Long memberId = requireMember(principal);
        Page<MemberCoupon> page = memberCouponRepository.findByMemberIdOrderByExpiresAtAsc(memberId, pageable);

        // N+1 회피용 간이 캐시 (코드 단순성 위해 페이지 단위 in-memory)
        List<Long> couponIds = page.stream().map(MemberCoupon::getCouponId).distinct().toList();
        Map<Long, Coupon> couponMap = new HashMap<>();
        couponRepository.findAllById(couponIds).forEach(c -> couponMap.put(c.getId(), c));

        return page.map(mc -> CouponView.of(mc, couponMap.get(mc.getCouponId())));
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
