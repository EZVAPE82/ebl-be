package com.elfbarlounge.domain.order.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.order.api.dto.CheckoutRequest;
import com.elfbarlounge.domain.order.api.dto.OrderView;
import com.elfbarlounge.domain.order.application.OrderService;
import com.elfbarlounge.domain.order.application.RefundService;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.Refund;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "Order")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RefundService refundService;

    @Operation(summary = "결제 (장바구니 → 주문)")
    @PostMapping("/checkout")
    public ResponseEntity<OrderView> checkout(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody CheckoutRequest req
    ) {
        Long memberId = requireMember(principal);
        Order o = orderService.checkout(memberId, req);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + o.getId()))
                .body(OrderView.from(o));
    }

    @Operation(summary = "내 주문 상세")
    @GetMapping("/{id}")
    public OrderView detail(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        return OrderView.from(orderService.getMyOrder(requireMember(principal), id));
    }

    @Operation(summary = "내 주문 목록")
    @GetMapping
    public Page<OrderView> list(@AuthenticationPrincipal AuthPrincipal principal, Pageable pageable) {
        return orderService.listMyOrders(requireMember(principal), pageable).map(OrderView::from);
    }

    @Operation(summary = "환불 신청")
    @PostMapping("/{id}/refund-requests")
    public ResponseEntity<Map<String, Object>> requestRefund(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody RefundRequestDto req
    ) {
        Refund r = refundService.requestRefund(requireMember(principal), id, req.reason());
        return ResponseEntity.ok(Map.of("refundId", r.getId(), "status", r.getStatus()));
    }

    public record RefundRequestDto(@NotBlank @Size(max = 200) String reason) {}

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
