package com.elfbarlounge.domain.point.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.point.domain.PointTransaction;
import com.elfbarlounge.domain.point.domain.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 적립금 원장 서비스.
 * - 모든 변경은 append-only (PointTransaction row 추가)
 * - 잔액 = sum(amount)
 * - 만료 6개월 기본
 *
 * 동시성: 매번 sumBalance 후 row 추가. 동시 요청 시 음수 잔액 가능 →
 *   운영 단계에서 SELECT ... FOR UPDATE 또는 분산락 추가 필요. (TODO 표시)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    public static final int DEFAULT_EXPIRY_MONTHS = 6;

    private final PointTransactionRepository pointRepository;

    @Transactional(readOnly = true)
    public long getBalance(Long memberId) {
        return pointRepository.sumBalance(memberId);
    }

    @Transactional
    public PointTransaction earn(Long memberId, long amount, String sourceType, Long sourceId, String memo) {
        if (amount <= 0) {
            throw ApiException.badRequest("POINT_AMOUNT_INVALID", "적립 금액은 0보다 커야 합니다.");
        }
        long after = getBalance(memberId) + amount;
        return pointRepository.save(PointTransaction.builder()
                .memberId(memberId)
                .type(PointTransaction.Type.EARN)
                .amount(amount)
                .balanceAfter(after)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .expiresAt(LocalDateTime.now().plusMonths(DEFAULT_EXPIRY_MONTHS))
                .memo(memo)
                .build());
    }

    @Transactional
    public PointTransaction use(Long memberId, long amount, String sourceType, Long sourceId, String memo) {
        if (amount <= 0) {
            throw ApiException.badRequest("POINT_AMOUNT_INVALID", "사용 금액은 0보다 커야 합니다.");
        }
        long balance = getBalance(memberId);
        if (balance < amount) {
            throw ApiException.badRequest("POINT_INSUFFICIENT", "보유 적립금이 부족합니다.");
        }
        long after = balance - amount;
        return pointRepository.save(PointTransaction.builder()
                .memberId(memberId)
                .type(PointTransaction.Type.USE)
                .amount(-amount)
                .balanceAfter(after)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .memo(memo)
                .build());
    }

    @Transactional
    public PointTransaction refund(Long memberId, long amount, String sourceType, Long sourceId, String memo) {
        if (amount <= 0) {
            throw ApiException.badRequest("POINT_AMOUNT_INVALID", "환불 금액은 0보다 커야 합니다.");
        }
        long after = getBalance(memberId) + amount;
        return pointRepository.save(PointTransaction.builder()
                .memberId(memberId)
                .type(PointTransaction.Type.REFUND)
                .amount(amount)
                .balanceAfter(after)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .memo(memo)
                .build());
    }
}
