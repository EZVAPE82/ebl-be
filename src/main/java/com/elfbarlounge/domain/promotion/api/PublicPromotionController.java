package com.elfbarlounge.domain.promotion.api;

import com.elfbarlounge.domain.promotion.domain.Promotion;
import com.elfbarlounge.domain.promotion.domain.PromotionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "PublicPromotion")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicPromotionController {

    private final PromotionRepository promotionRepository;

    /**
     * 상품에 적용 가능한 활성 프로모션 — 상품 상세 페이지에서 "2+1 진행 중" 뱃지 표시용.
     */
    @Operation(summary = "상품별 활성 프로모션 (공개)")
    @GetMapping("/products/{productId}/promotions")
    public List<PromotionBadge> productPromotions(@PathVariable Long productId) {
        return promotionRepository.findActiveByProductId(productId, LocalDateTime.now()).stream()
                .map(PromotionBadge::from)
                .toList();
    }

    public record PromotionBadge(
            Long id,
            String name,
            int buyQuantity,
            int getQuantity,
            String label    // "2+1", "10+1"
    ) {
        public static PromotionBadge from(Promotion p) {
            return new PromotionBadge(
                    p.getId(), p.getName(), p.getBuyQuantity(), p.getGetQuantity(),
                    p.getBuyQuantity() + "+" + p.getGetQuantity()
            );
        }
    }
}
