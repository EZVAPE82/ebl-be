package com.elfbarlounge.domain.promotion.api;

import com.elfbarlounge.domain.promotion.application.PromotionService;
import com.elfbarlounge.domain.promotion.domain.Promotion;
import com.elfbarlounge.domain.promotion.domain.PromotionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Set;

@Tag(name = "AdminPromotion")
@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionService promotionService;
    private final PromotionRepository promotionRepository;

    @Operation(summary = "[Admin] 프로모션 목록")
    @GetMapping
    public Page<PromotionView> list(Pageable pageable) {
        return promotionRepository.findAllByOrderByCreatedAtDesc(pageable).map(PromotionView::from);
    }

    @Operation(summary = "[Admin] 프로모션 단건")
    @GetMapping("/{id}")
    public PromotionView get(@PathVariable Long id) {
        return PromotionView.from(promotionRepository.findById(id).orElseThrow());
    }

    @Operation(summary = "[Admin] 프로모션 생성")
    @PostMapping
    public ResponseEntity<PromotionView> create(@Valid @RequestBody CreateRequest req) {
        Promotion p = promotionService.create(
                req.name(),
                req.type(),
                req.buyQuantity(),
                req.getQuantity(),
                req.giftProductId(),
                req.giftProductOptionId(),
                req.validFrom(),
                req.validTo(),
                req.productIds()
        );
        return ResponseEntity.created(URI.create("/api/v1/admin/promotions/" + p.getId()))
                .body(PromotionView.from(p));
    }

    @Operation(summary = "[Admin] 프로모션 비활성화")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        promotionService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRequest(
            @NotBlank String name,
            @NotNull Promotion.Type type,
            @Positive int buyQuantity,
            @Positive int getQuantity,
            Long giftProductId,
            Long giftProductOptionId,
            @NotNull LocalDateTime validFrom,
            @NotNull LocalDateTime validTo,
            @NotNull Set<Long> productIds
    ) {}

    public record PromotionView(
            Long id, String name, String type,
            int buyQuantity, int getQuantity,
            Long giftProductId, Long giftProductOptionId,
            LocalDateTime validFrom, LocalDateTime validTo,
            boolean active, Set<Long> productIds,
            LocalDateTime createdAt
    ) {
        public static PromotionView from(Promotion p) {
            return new PromotionView(
                    p.getId(), p.getName(), p.getType().name(),
                    p.getBuyQuantity(), p.getGetQuantity(),
                    p.getGiftProductId(), p.getGiftProductOptionId(),
                    p.getValidFrom(), p.getValidTo(),
                    p.isActive(), p.getProductIds(),
                    p.getCreatedAt()
            );
        }
    }
}
