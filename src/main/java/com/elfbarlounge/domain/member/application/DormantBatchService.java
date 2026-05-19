package com.elfbarlounge.domain.member.application;

import com.elfbarlounge.domain.member.domain.MemberStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 1년 미접속 회원 휴면 전환 배치 (정보통신망법 §29-2).
 * 매일 새벽 4시 실행.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DormantBatchService {

    @PersistenceContext
    private EntityManager em;

    /**
     * 1년 미접속(lastLoginAt) ACTIVE 회원을 DORMANT로 전환.
     * lastLoginAt이 NULL이면 createdAt 기준.
     */
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public int convertToDormant() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        int updated = em.createQuery(
                """
                UPDATE Member m
                   SET m.status = :dormant
                 WHERE m.status = :active
                   AND ( (m.lastLoginAt IS NOT NULL AND m.lastLoginAt < :threshold)
                      OR (m.lastLoginAt IS NULL AND m.createdAt < :threshold) )
                """)
                .setParameter("dormant", MemberStatus.DORMANT)
                .setParameter("active", MemberStatus.ACTIVE)
                .setParameter("threshold", threshold)
                .executeUpdate();
        log.info("Dormant batch converted {} members (threshold={})", updated, threshold);
        return updated;
    }
}
