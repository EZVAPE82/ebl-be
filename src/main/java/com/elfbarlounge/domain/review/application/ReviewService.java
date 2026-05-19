package com.elfbarlounge.domain.review.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderItem;
import com.elfbarlounge.domain.order.domain.OrderRepository;
import com.elfbarlounge.domain.order.domain.OrderStatus;
import com.elfbarlounge.domain.point.application.PointService;
import com.elfbarlounge.domain.review.domain.Review;
import com.elfbarlounge.domain.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 리뷰 작성 + 자동 적립.
 * 룰:
 *  - 주문 상태 DELIVERED 만 작성 가능
 *  - 배송완료 후 7일 이내 작성분만 적립 (어드민 설정값 가능)
 *  - 1 OrderItem 당 1 리뷰
 *  - 적립 금액: 텍스트만 500P, 포토리뷰 1000P (MVP 고정값; 추후 어드민화)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final long POINT_TEXT = 500;
    private static final long POINT_PHOTO = 1000;
    private static final int REVIEW_WINDOW_DAYS = 7;

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final PointService pointService;

    @Transactional
    public Review write(Long memberId, Long orderItemId, int rating, String content, List<String> photoUrls) {
        if (rating < 1 || rating > 5) {
            throw ApiException.badRequest("RATING_INVALID", "별점은 1~5 사이여야 합니다.");
        }
        if (reviewRepository.findByOrderItemId(orderItemId).isPresent()) {
            throw ApiException.conflict("REVIEW_DUPLICATED", "이미 작성된 리뷰입니다.");
        }

        // 주문 찾기 + 검증
        OrderInfo info = findOrderItemForMember(memberId, orderItemId);

        Review review = Review.builder()
                .productId(info.productId)
                .memberId(memberId)
                .orderItemId(orderItemId)
                .rating(rating)
                .content(content)
                .build();
        if (photoUrls != null) {
            int idx = 0;
            for (String url : photoUrls) {
                review.addPhoto(url, idx++);
            }
        }
        review = reviewRepository.save(review);

        // 적립 (배송완료 + 7일 이내)
        if (info.eligibleForReward) {
            long amount = (photoUrls != null && !photoUrls.isEmpty()) ? POINT_PHOTO : POINT_TEXT;
            pointService.earn(memberId, amount, "REVIEW", review.getId(),
                    review.isHasPhoto() ? "포토 리뷰 적립" : "텍스트 리뷰 적립");
            review.markPointRewarded();
        }

        return review;
    }

    private OrderInfo findOrderItemForMember(Long memberId, Long orderItemId) {
        // OrderItem을 OrderRepository 통해 찾기 위해 전체 주문에서 검색 — MVP 단순 구현
        // 운영에서는 OrderItemRepository 추가 권장.
        List<Order> orders = orderRepository.findAll();
        for (Order o : orders) {
            if (!o.getMemberId().equals(memberId)) continue;
            for (OrderItem item : o.getItems()) {
                if (item.getId().equals(orderItemId)) {
                    if (o.getStatus() != OrderStatus.DELIVERED) {
                        throw ApiException.badRequest("ORDER_NOT_DELIVERED", "배송 완료된 주문만 리뷰 작성 가능합니다.");
                    }
                    boolean eligible = o.getUpdatedAt() == null || Duration.between(o.getUpdatedAt(), LocalDateTime.now()).toDays() <= REVIEW_WINDOW_DAYS;
                    return new OrderInfo(item.getProductId(), eligible);
                }
            }
        }
        throw ApiException.notFound("ORDER_ITEM_NOT_FOUND", "주문 항목을 찾을 수 없습니다.");
    }

    private record OrderInfo(Long productId, boolean eligibleForReward) {}
}
