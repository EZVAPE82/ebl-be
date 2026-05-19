package com.elfbarlounge.domain.product.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.product.application.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Wishlist")
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "위시리스트 등록 (회원 한정)")
    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> add(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long productId
    ) {
        Long memberId = requireMember(principal);
        Long id = wishlistService.add(memberId, productId);
        return ResponseEntity.ok(Map.of("id", id, "added", true));
    }

    @Operation(summary = "위시리스트 해제")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long productId
    ) {
        Long memberId = requireMember(principal);
        wishlistService.remove(memberId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 위시리스트 (productId 목록)")
    @GetMapping
    public Page<Long> list(
            @AuthenticationPrincipal AuthPrincipal principal,
            Pageable pageable
    ) {
        Long memberId = requireMember(principal);
        return wishlistService.productIds(memberId, pageable);
    }

    @Operation(summary = "위시리스트 개수")
    @GetMapping("/count")
    public Map<String, Object> count(@AuthenticationPrincipal AuthPrincipal principal) {
        Long memberId = requireMember(principal);
        return Map.of("count", wishlistService.count(memberId));
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) {
            throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        }
        return p.memberId();
    }
}
