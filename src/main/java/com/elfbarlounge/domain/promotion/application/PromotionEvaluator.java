package com.elfbarlounge.domain.promotion.application;

import com.elfbarlounge.domain.promotion.domain.Promotion;
import com.elfbarlounge.domain.promotion.domain.PromotionRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 프로모션 평가 — 장바구니/결제 직전 호출되어 무료 증정 라인 산출.
 *
 * 정책:
 *  - 트리거 상품 N 구매 시 floor(N / buy) * get 개수 증정
 *  - 동일 상품(BOGO_SAME): 같은 productId/optionId 라인 추가, unit_price=0
 *  - 별도 상품(BOGO_OTHER): gift_product_id 라인 추가
 *  - 한 상품에 여러 프로모션 매칭 시: 첫 매칭만 적용 (단순화, 추후 우선순위 컬럼 추가 가능)
 *
 * 호출:
 *  - CartService 미리보기 (장바구니 표시용, 비-영속)
 *  - OrderService.checkout (실제 결제 시 영속)
 */
@Service
@RequiredArgsConstructor
public class PromotionEvaluator {

    private final PromotionRepository promotionRepository;

    /**
     * 주문 라인 후보 (productId, optionId, quantity) 목록을 받아
     * 적용 가능한 프로모션과 무료 라인을 계산해 반환.
     */
    @Transactional(readOnly = true)
    public List<AppliedPromotion> evaluate(List<PurchaseLine> lines) {
        List<AppliedPromotion> applied = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (PurchaseLine line : lines) {
            List<Promotion> promos = promotionRepository.findActiveByProductId(line.productId(), now);
            for (Promotion p : promos) {
                if (!p.isApplicableNow()) continue;
                int freeQty = p.calcFreeQuantity(line.quantity());
                if (freeQty <= 0) continue;

                Long giftProductId = p.getType() == Promotion.Type.BOGO_SAME
                        ? line.productId()
                        : p.getGiftProductId();
                Long giftOptionId = p.getType() == Promotion.Type.BOGO_SAME
                        ? line.productOptionId()
                        : p.getGiftProductOptionId();

                applied.add(AppliedPromotion.builder()
                        .promotionId(p.getId())
                        .promotionName(p.getName())
                        .triggerProductId(line.productId())
                        .triggerQuantity(line.quantity())
                        .giftProductId(giftProductId)
                        .giftProductOptionId(giftOptionId)
                        .giftQuantity(freeQty)
                        .build());
                break; // 한 상품에 1 프로모션 (첫 매칭)
            }
        }
        return applied;
    }

    public record PurchaseLine(Long productId, Long productOptionId, int quantity) {}

    @Builder
    @Getter
    public static class AppliedPromotion {
        private final Long promotionId;
        private final String promotionName;
        private final Long triggerProductId;
        private final int triggerQuantity;
        private final Long giftProductId;
        private final Long giftProductOptionId;
        private final int giftQuantity;
    }
}
