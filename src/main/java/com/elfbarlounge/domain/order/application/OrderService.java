package com.elfbarlounge.domain.order.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.cart.domain.Cart;
import com.elfbarlounge.domain.cart.domain.CartItem;
import com.elfbarlounge.domain.cart.domain.CartRepository;
import com.elfbarlounge.domain.coupon.application.CouponService;
import com.elfbarlounge.domain.order.api.dto.CheckoutRequest;
import com.elfbarlounge.domain.order.application.payment.PaymentGateway;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderItem;
import com.elfbarlounge.domain.order.domain.OrderRepository;
import com.elfbarlounge.domain.order.domain.OrderStatus;
import com.elfbarlounge.domain.order.domain.Payment;
import com.elfbarlounge.domain.order.domain.PaymentRepository;
import com.elfbarlounge.domain.point.application.PointService;
import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductOption;
import com.elfbarlounge.domain.product.domain.ProductRepository;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 주문 생성 흐름:
 *  1. 장바구니 → 가격·재고 검증, 라인 스냅샷
 *  2. 배송비 계산 (정책 설정값, MVP: 0)
 *  3. 쿠폰·적립금 차감
 *  4. Order ROW 생성 (status=PENDING_PAYMENT)
 *  5. 재고 차감 (옵션별)
 *  6. PG 결제 호출 (stub)
 *  7. 성공 → Payment 기록, Order=PAID, 장바구니 비우기
 *  8. 실패 → 쿠폰·적립금 복구, 재고 복구, Order=CANCELED
 *
 * 동시성 주의: 재고는 옵션 row를 SELECT FOR UPDATE 또는 낙관락이 필요.
 *  현재는 JPA dirty-checking 기반 (트랜잭션 격리 SERIALIZABLE 아님).
 *  운영 단계에서 @Version 추가 또는 비관락 검토.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponService couponService;
    private final PointService pointService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public Order checkout(Long memberId, CheckoutRequest req) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> ApiException.badRequest("CART_EMPTY", "장바구니가 비어 있습니다."));
        if (cart.getItems().isEmpty()) {
            throw ApiException.badRequest("CART_EMPTY", "장바구니가 비어 있습니다.");
        }

        // 1. 라인 스냅샷 + 재고 차감
        long productAmount = 0;
        Order order = Order.builder()
                .orderNo(generateOrderNo())
                .memberId(memberId)
                .channel("SELF")
                .productAmount(0)
                .shippingFee(0)
                .discountAmount(0)
                .pointUsed(0)
                .memberCouponId(req.memberCouponId())
                .recipientName(req.recipientName())
                .recipientPhone(req.recipientPhone())
                .postalCode(req.postalCode())
                .address1(req.address1())
                .address2(req.address2())
                .memo(req.memo())
                .build();

        for (CartItem ci : cart.getItems()) {
            Product p = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
            if (p.getStatus() != ProductStatus.ACTIVE) {
                throw ApiException.badRequest("PRODUCT_NOT_AVAILABLE",
                        "구매할 수 없는 상품이 포함되어 있습니다: " + p.getName());
            }

            long unitPrice = p.getPrice();
            String optionText = null;
            if (ci.getProductOptionId() != null) {
                ProductOption opt = p.getOptions().stream()
                        .filter(o -> o.getId().equals(ci.getProductOptionId()))
                        .findFirst()
                        .orElseThrow(() -> ApiException.badRequest("OPTION_NOT_FOUND",
                                "선택한 옵션을 찾을 수 없습니다."));
                unitPrice += opt.getPriceDelta();
                optionText = opt.getOptionGroup() + ": " + opt.getOptionValue();
                opt.decreaseStock(ci.getQuantity());
            }

            order.addItem(OrderItem.builder()
                    .productId(p.getId())
                    .productOptionId(ci.getProductOptionId())
                    .productName(p.getName())
                    .optionText(optionText)
                    .unitPrice(unitPrice)
                    .quantity(ci.getQuantity())
                    .build());
            productAmount += unitPrice * ci.getQuantity();
        }

        // 2. 쿠폰 + 3. 적립금
        long discountAmount = 0;
        if (req.memberCouponId() != null) {
            // 주문 ID는 저장 후 알 수 있음 → 우선 임시 -1로 사용, 추후 보정
            discountAmount = couponService.useCoupon(memberId, req.memberCouponId(), productAmount, -1L);
        }
        long pointUsed = req.pointUsed();
        if (pointUsed > 0) {
            pointService.use(memberId, pointUsed, "ORDER", null,
                    "주문 사용");
        }
        long shippingFee = 0; // TODO: 정책 설정값 적용 (어드민 free_shipping_threshold)

        long paidAmount = productAmount + shippingFee - discountAmount - pointUsed;
        if (paidAmount < 0) {
            throw ApiException.badRequest("AMOUNT_INVALID", "결제 금액이 잘못되었습니다.");
        }

        // 4. Order 저장 (PENDING_PAYMENT 상태)
        order = persistOrder(order, productAmount, shippingFee, discountAmount, pointUsed);

        // 5. PG 결제
        PaymentGateway.PaymentResult result = paymentGateway.charge(
                order.getOrderNo(), paidAmount, req.paymentToken(), req.paymentMethod());

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .pgProvider("PORTONE")
                .method(req.paymentMethod())
                .amount(paidAmount)
                .status(Payment.Status.READY)
                .build();
        paymentRepository.save(payment);

        if (!result.ok()) {
            // 실패 → 롤백 (재고·쿠폰·적립금 복구는 트랜잭션 자동 롤백으로 처리)
            throw ApiException.badRequest("PAYMENT_FAILED", "결제에 실패했습니다.");
        }

        payment.markPaid(result.pgTxId(), result.rawResponse());
        order.markPaid();

        // 장바구니 비우기
        cart.clear();

        log.info("Order created: orderNo={} memberId={} paidAmount={}", order.getOrderNo(), memberId, paidAmount);
        return order;
    }

    private Order persistOrder(Order draft, long productAmount, long shippingFee, long discountAmount, long pointUsed) {
        Order toSave = Order.builder()
                .orderNo(draft.getOrderNo())
                .memberId(draft.getMemberId())
                .channel(draft.getChannel())
                .productAmount(productAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .pointUsed(pointUsed)
                .memberCouponId(draft.getMemberCouponId())
                .recipientName(draft.getRecipientName())
                .recipientPhone(draft.getRecipientPhone())
                .postalCode(draft.getPostalCode())
                .address1(draft.getAddress1())
                .address2(draft.getAddress2())
                .memo(draft.getMemo())
                .build();
        draft.getItems().forEach(toSave::addItem);
        return orderRepository.save(toSave);
    }

    @Transactional(readOnly = true)
    public Order getMyOrder(Long memberId, Long orderId) {
        Order o = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));
        o.getItems().size();
        return o;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Order> listMyOrders(Long memberId, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Order> page = orderRepository.findByMemberIdOrderByOrderedAtDesc(memberId, pageable);
        page.forEach(o -> o.getItems().size());
        return page;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Order> adminList(OrderStatus status, org.springframework.data.domain.Pageable pageable) {
        if (status != null) {
            org.springframework.data.domain.Page<Order> page = orderRepository.findByStatusOrderByOrderedAtDesc(status, pageable);
            page.forEach(o -> o.getItems().size());
            return page;
        }
        org.springframework.data.domain.Page<Order> page = orderRepository.findAll(pageable);
        page.forEach(o -> o.getItems().size());
        return page;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus next) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));
        switch (next) {
            case PREPARING -> o.markPreparing();
            case SHIPPING -> o.markShipping();
            case DELIVERED -> o.markDelivered();
            case CANCELED -> o.markCanceled();
            default -> throw ApiException.badRequest("INVALID_STATUS", "상태 전이가 허용되지 않습니다.");
        }
        return o;
    }

    private String generateOrderNo() {
        String ymd = java.time.LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return ymd + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
