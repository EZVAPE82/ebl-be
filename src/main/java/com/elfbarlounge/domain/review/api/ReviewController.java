package com.elfbarlounge.domain.review.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.review.application.ReviewService;
import com.elfbarlounge.domain.review.domain.Review;
import com.elfbarlounge.domain.review.domain.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Review")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @Operation(summary = "리뷰 작성 (회원, 배송완료 주문 한정)")
    @PostMapping("/reviews")
    public ResponseEntity<ReviewView> write(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody WriteRequest req
    ) {
        Long memberId = requireMember(principal);
        Review r = reviewService.write(memberId, req.orderItemId(), req.rating(), req.content(), req.photoUrls());
        return ResponseEntity.created(URI.create("/api/v1/reviews/" + r.getId()))
                .body(ReviewView.from(r));
    }

    @Operation(summary = "상품별 리뷰 목록 (공개)")
    @GetMapping("/public/products/{productId}/reviews")
    public Page<ReviewView> productReviews(@PathVariable Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable).map(ReviewView::from);
    }

    @Operation(summary = "베스트 리뷰 목록 (공개) — 별점 4+, 사진 있는 것 우선")
    @GetMapping("/public/reviews/best")
    public Page<ReviewView> bestReviews(Pageable pageable) {
        return reviewRepository.findBestPublic(pageable).map(ReviewView::from);
    }

    @Operation(summary = "내 리뷰 목록")
    @GetMapping("/members/me/reviews")
    public Page<ReviewView> myReviews(@AuthenticationPrincipal AuthPrincipal principal, Pageable pageable) {
        return reviewRepository.findByMemberIdOrderByCreatedAtDesc(requireMember(principal), pageable).map(ReviewView::from);
    }

    public record WriteRequest(
            @NotNull Long orderItemId,
            @NotNull @Min(1) @Max(5) Integer rating,
            @Size(max = 2000) String content,
            List<@Size(max = 1000) String> photoUrls
    ) {}

    public record ReviewView(
            Long id, Long productId, Long memberId, int rating, String content,
            boolean hasPhoto, List<String> photoUrls, boolean pointRewarded, LocalDateTime createdAt
    ) {
        public static ReviewView from(Review r) {
            List<String> urls = r.getPhotos() == null ? List.of()
                    : r.getPhotos().stream().map(p -> p.getThumbnailUrl() != null ? p.getThumbnailUrl() : p.getUrl()).toList();
            return new ReviewView(r.getId(), r.getProductId(), r.getMemberId(),
                    r.getRating(), r.getContent(), r.isHasPhoto(), urls, r.isPointRewarded(), r.getCreatedAt());
        }
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
