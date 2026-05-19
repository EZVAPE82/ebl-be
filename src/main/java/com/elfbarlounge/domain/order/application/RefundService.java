package com.elfbarlounge.domain.order.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.coupon.application.CouponService;
import com.elfbarlounge.domain.order.application.payment.PaymentGateway;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderRepository;
import com.elfbarlounge.domain.order.domain.OrderStatus;
import com.elfbarlounge.domain.order.domain.Payment;
import com.elfbarlounge.domain.order.domain.PaymentRepository;
import com.elfbarlounge.domain.order.domain.Refund;
import com.elfbarlounge.domain.order.domain.RefundRepository;
import com.elfbarlounge.domain.point.application.PointService;
import com.elfbarlounge.domain.settings.application.PolicySettingsService;
import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductOption;
import com.elfbarlounge.domain.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 환불·취소.
 * MVP: 전체 환불만 지원 (부분환불 X — Out of Scope)
 *
 * 흐름:
 *  1. 회원이 환불 신청 (Refund REQUESTED)
 *  2. 어드민 승인 시 PG 취소 → 적립금 복구 → 쿠폰 복구 → 재고 복구 → Order=REFUNDED
 *  3. 반송 택배비는 환불금액에서 차감 (어드민 설정값, MVP: 0)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CouponService couponService;
    private final PointService pointService;
    private final PaymentGateway paymentGateway;
    private final PolicySettingsService policySettingsService;

    @Transactional
    public Refund requestRefund(Long memberId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));
        if (order.getStatus() == OrderStatus.REFUNDED || order.getStatus() == OrderStatus.CANCELED) {
            throw ApiException.conflict("ORDER_ALREADY_FINAL", "이미 종결된 주문입니다.");
        }
        long shippingFeeDeducted = policySettingsService.getLong(
                PolicySettingsService.RETURN_SHIPPING_FEE, 3000);
        long refundAmount = order.getPaidAmount() - shippingFeeDeducted;
        return refundRepository.save(Refund.builder()
                .orderId(orderId)
                .reason(reason)
                .amount(refundAmount)
                .shippingFeeDeducted(shippingFeeDeducted)
                .build());
    }

    @Transactional
    public void approve(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> ApiException.notFound("REFUND_NOT_FOUND", "환불 신청을 찾을 수 없습니다."));
        if (refund.getStatus() != Refund.Status.REQUESTED) {
            throw ApiException.conflict("REFUND_ALREADY_REVIEWED", "이미 처리된 신청입니다.");
        }

        Order order = orderRepository.findById(refund.getOrderId())
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId())
                .orElseThrow(() -> ApiException.notFound("PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다."));

        // 1. PG 취소
        paymentGateway.cancel(payment.getPgTxId(), refund.getAmount(), refund.getReason());
        payment.markRefunded();

        // 2. 적립금 복구 (회원이 결제 시 사용한 적립금)
        if (order.getPointUsed() > 0) {
            pointService.refund(order.getMemberId(), order.getPointUsed(),
                    "ORDER_REFUND", order.getId(), "주문 환불에 의한 적립금 복구");
        }

        // 3. 쿠폰 복구
        if (order.getMemberCouponId() != null) {
            couponService.revertCoupon(order.getMemberCouponId());
        }

        // 4. 재고 복구
        order.getItems().forEach(item -> {
            if (item.getProductOptionId() != null) {
                productRepository.findById(item.getProductId()).ifPresent(p ->
                        p.getOptions().stream()
                                .filter(o -> o.getId().equals(item.getProductOptionId()))
                                .findFirst()
                                .ifPresent(opt -> incrementStock(opt, item.getQuantity())));
            }
        });

        order.markRefunded();
        refund.approve();
        refund.complete();
        log.info("Refund completed: refundId={}, orderId={}, amount={}", refundId, order.getId(), refund.getAmount());
    }

    @Transactional
    public void reject(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> ApiException.notFound("REFUND_NOT_FOUND", "환불 신청을 찾을 수 없습니다."));
        refund.reject();
    }

    private void incrementStock(ProductOption option, int qty) {
        option.increaseStock(qty);
    }
}
