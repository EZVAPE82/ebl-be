package com.elfbarlounge.domain.cart.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.cart.application.CartService;
import com.elfbarlounge.domain.cart.domain.Cart;
import com.elfbarlounge.domain.cart.domain.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Cart")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "내 장바구니 조회")
    @GetMapping
    public CartResponse view(@AuthenticationPrincipal AuthPrincipal principal) {
        return CartResponse.from(cartService.view(requireMember(principal)));
    }

    @Operation(summary = "담기")
    @PostMapping
    public CartResponse add(@AuthenticationPrincipal AuthPrincipal principal, @Valid @RequestBody AddRequest req) {
        return CartResponse.from(cartService.addItem(requireMember(principal), req.productId(), req.productOptionId(), req.quantity()));
    }

    @Operation(summary = "수량 변경")
    @PutMapping("/items/{itemId}")
    public CartResponse update(@AuthenticationPrincipal AuthPrincipal principal,
                               @PathVariable Long itemId, @Valid @RequestBody UpdateRequest req) {
        return CartResponse.from(cartService.updateItem(requireMember(principal), itemId, req.quantity()));
    }

    @Operation(summary = "단건 삭제")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long itemId) {
        cartService.removeItem(requireMember(principal), itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 비우기")
    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal AuthPrincipal principal) {
        cartService.clear(requireMember(principal));
        return ResponseEntity.noContent().build();
    }

    public record AddRequest(@NotNull Long productId, Long productOptionId, @NotNull @Min(1) Integer quantity) {}
    public record UpdateRequest(@NotNull @Min(1) Integer quantity) {}

    public record CartResponse(Long id, Long memberId, List<ItemView> items) {
        public static CartResponse from(Cart c) {
            return new CartResponse(c.getId(), c.getMemberId(),
                    c.getItems().stream().map(ItemView::from).toList());
        }
    }
    public record ItemView(Long id, Long productId, Long productOptionId, int quantity) {
        public static ItemView from(CartItem i) {
            return new ItemView(i.getId(), i.getProductId(), i.getProductOptionId(), i.getQuantity());
        }
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
