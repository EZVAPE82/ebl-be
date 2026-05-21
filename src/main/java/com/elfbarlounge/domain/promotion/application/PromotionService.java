package com.elfbarlounge.domain.promotion.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.promotion.domain.Promotion;
import com.elfbarlounge.domain.promotion.domain.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Transactional
    public Promotion create(String name, Promotion.Type type,
                            int buyQuantity, int getQuantity,
                            Long giftProductId, Long giftProductOptionId,
                            LocalDateTime validFrom, LocalDateTime validTo,
                            Set<Long> productIds) {
        validate(name, type, buyQuantity, getQuantity, giftProductId, validFrom, validTo, productIds);
        Promotion p = Promotion.builder()
                .name(name)
                .type(type)
                .buyQuantity(buyQuantity)
                .getQuantity(getQuantity)
                .giftProductId(giftProductId)
                .giftProductOptionId(giftProductOptionId)
                .validFrom(validFrom)
                .validTo(validTo)
                .active(true)
                .productIds(productIds)
                .build();
        return promotionRepository.save(p);
    }

    @Transactional
    public void deactivate(Long promotionId) {
        Promotion p = promotionRepository.findById(promotionId)
                .orElseThrow(() -> ApiException.notFound("PROMO_NOT_FOUND", "프로모션을 찾을 수 없습니다."));
        p.deactivate();
    }

    private void validate(String name, Promotion.Type type, int buy, int get,
                          Long giftProductId, LocalDateTime from, LocalDateTime to, Set<Long> productIds) {
        if (name == null || name.isBlank()) {
            throw ApiException.badRequest("PROMO_NAME_REQUIRED", "프로모션 이름을 입력해주세요.");
        }
        if (buy <= 0 || get <= 0) {
            throw ApiException.badRequest("PROMO_QTY_INVALID", "구매·증정 수량은 1 이상이어야 합니다.");
        }
        if (from == null || to == null || !from.isBefore(to)) {
            throw ApiException.badRequest("PROMO_PERIOD_INVALID", "유효 기간이 올바르지 않습니다.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw ApiException.badRequest("PROMO_NO_PRODUCT", "최소 1개 이상의 적용 상품을 선택해주세요.");
        }
        if (type == Promotion.Type.BOGO_OTHER && giftProductId == null) {
            throw ApiException.badRequest("PROMO_GIFT_REQUIRED",
                    "BOGO_OTHER 는 사은품 상품을 지정해야 합니다.");
        }
    }
}
