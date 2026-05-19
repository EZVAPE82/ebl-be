package com.elfbarlounge.domain.point.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.point.domain.PointTransaction;
import com.elfbarlounge.domain.point.domain.PointTransactionRepository;
import com.elfbarlounge.domain.point.application.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Point")
@RestController
@RequestMapping("/api/v1/members/me/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final PointTransactionRepository pointRepository;

    @Operation(summary = "내 적립금 잔액")
    @GetMapping("/balance")
    public Map<String, Object> balance(@AuthenticationPrincipal AuthPrincipal principal) {
        Long memberId = requireMember(principal);
        return Map.of("balance", pointService.getBalance(memberId));
    }

    @Operation(summary = "내 적립금 이력")
    @GetMapping
    public Page<PointHistoryView> history(@AuthenticationPrincipal AuthPrincipal principal, Pageable pageable) {
        Long memberId = requireMember(principal);
        return pointRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(PointHistoryView::from);
    }

    public record PointHistoryView(
            Long id, PointTransaction.Type type, long amount, long balanceAfter,
            String sourceType, Long sourceId, String memo,
            LocalDateTime expiresAt, LocalDateTime createdAt
    ) {
        public static PointHistoryView from(PointTransaction p) {
            return new PointHistoryView(
                    p.getId(), p.getType(), p.getAmount(), p.getBalanceAfter(),
                    p.getSourceType(), p.getSourceId(), p.getMemo(),
                    p.getExpiresAt(), p.getCreatedAt()
            );
        }
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
