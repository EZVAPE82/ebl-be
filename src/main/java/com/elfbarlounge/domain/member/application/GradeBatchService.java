package com.elfbarlounge.domain.member.application;

import com.elfbarlounge.domain.member.domain.GradeCode;
import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberRepository;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderRepository;
import com.elfbarlounge.domain.order.domain.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 회원 등급 산정 배치 — 매월 1일 새벽 5시 실행.
 * 6개월 누적 PAID/DELIVERED 금액 기반으로 GradeCode 산정.
 * 결과는 멤버 테이블에 직접 저장하지 않음 (현재 스키마에 grade 컬럼 미존재; member_grades 별도 테이블로 확장 시 추가).
 *
 * MVP: 결과를 로그·메모리로만 남김. 실제 등급 컬럼/테이블 적용은 다음 마이그레이션.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradeBatchService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager em;

    @Scheduled(cron = "0 0 5 1 * *")
    @Transactional(readOnly = true)
    public Map<Long, GradeCode> recomputeAll() {
        LocalDateTime threshold = LocalDate.now().minusMonths(6).atStartOfDay();
        // 누적액 계산
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createQuery(
                """
                SELECT o.memberId, SUM(o.paidAmount)
                  FROM Order o
                 WHERE o.orderedAt >= :threshold
                   AND o.status IN (:paid, :preparing, :shipping, :delivered)
                 GROUP BY o.memberId
                """)
                .setParameter("threshold", threshold)
                .setParameter("paid", OrderStatus.PAID)
                .setParameter("preparing", OrderStatus.PREPARING)
                .setParameter("shipping", OrderStatus.SHIPPING)
                .setParameter("delivered", OrderStatus.DELIVERED)
                .getResultList();

        Map<Long, GradeCode> map = new HashMap<>();
        for (Object[] r : rows) {
            Long memberId = (Long) r[0];
            long sum = ((Number) r[1]).longValue();
            map.put(memberId, GradeCode.fromAmount(sum));
        }
        log.info("Grade recomputed for {} members (window from {})", map.size(), threshold);
        // TODO: member_grades 테이블 (월별 스냅샷) 마이그레이션 추가 후 저장
        return map;
    }
}
