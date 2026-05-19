package com.elfbarlounge.domain.order.api;

import com.elfbarlounge.domain.order.api.dto.OrderView;
import com.elfbarlounge.domain.order.application.OrderService;
import com.elfbarlounge.domain.order.application.RefundService;
import com.elfbarlounge.domain.order.application.ShipmentService;
import com.elfbarlounge.domain.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminOrder")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final RefundService refundService;

    @Operation(summary = "[Admin] 주문 목록")
    @GetMapping
    public Page<OrderView> list(@RequestParam(required = false) OrderStatus status, Pageable pageable) {
        return orderService.adminList(status, pageable).map(OrderView::from);
    }

    @Operation(summary = "[Admin] 상태 전환")
    @PostMapping("/{id}/status/{next}")
    public OrderView updateStatus(@PathVariable Long id, @PathVariable OrderStatus next) {
        return OrderView.from(orderService.updateStatus(id, next));
    }

    @Operation(summary = "[Admin] 송장 입력 (배송 시작)")
    @PostMapping("/{id}/ship")
    public ResponseEntity<Void> ship(@PathVariable Long id, @Valid @RequestBody ShipRequest req) {
        shipmentService.ship(id, req.courier(), req.trackingNo());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 배송완료")
    @PostMapping("/{id}/deliver")
    public ResponseEntity<Void> deliver(@PathVariable Long id) {
        shipmentService.markDelivered(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 환불 승인")
    @PostMapping("/refunds/{refundId}/approve")
    public ResponseEntity<Void> approveRefund(@PathVariable Long refundId) {
        refundService.approve(refundId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 환불 반려")
    @PostMapping("/refunds/{refundId}/reject")
    public ResponseEntity<Void> rejectRefund(@PathVariable Long refundId) {
        refundService.reject(refundId);
        return ResponseEntity.noContent().build();
    }

    public record ShipRequest(
            @NotBlank @Size(max = 40) String courier,
            @NotBlank @Size(max = 64) String trackingNo
    ) {}
}
