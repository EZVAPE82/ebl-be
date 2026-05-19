package com.elfbarlounge.domain.point.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointTransaction p WHERE p.memberId = :memberId")
    long sumBalance(@Param("memberId") Long memberId);

    Page<PointTransaction> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
