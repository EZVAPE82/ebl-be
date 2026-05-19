package com.elfbarlounge.domain.coupon.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.coupon.api.dto.CouponRequest;
import com.elfbarlounge.domain.coupon.application.CouponService;
import com.elfbarlounge.domain.coupon.domain.Coupon;
import com.elfbarlounge.domain.coupon.domain.CouponRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "AdminCoupon")
@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Operation(summary = "[Admin] 쿠폰 정의 생성")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CouponRequest req) {
        if (req.code() != null && couponRepository.findByCode(req.code()).isPresent()) {
            throw ApiException.conflict("COUPON_CODE_DUPLICATED", "동일 코드의 쿠폰이 존재합니다.");
        }
        Coupon c = Coupon.builder()
                .code(req.code())
                .name(req.name())
                .type(req.type())
                .discountType(req.discountType())
                .discountValue(req.discountValue())
                .minOrderAmount(req.minOrderAmount())
                .maxDiscount(req.maxDiscount())
                .validDays(req.validDays())
                .active(req.active())
                .build();
        Long id = couponRepository.save(c).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/coupons/" + id))
                .body(Map.of("id", id));
    }

    @Operation(summary = "[Admin] 특정 회원에게 쿠폰 발급")
    @PostMapping("/{couponId}/issue/{memberId}")
    public ResponseEntity<Map<String, Object>> issue(@PathVariable Long couponId, @PathVariable Long memberId) {
        Long mcId = couponService.issueDirectly(memberId, couponId).getId();
        return ResponseEntity.ok(Map.of("memberCouponId", mcId));
    }
}
