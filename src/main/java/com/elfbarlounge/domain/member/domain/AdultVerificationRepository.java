package com.elfbarlounge.domain.member.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdultVerificationRepository extends JpaRepository<AdultVerification, Long> {

    Page<AdultVerification> findByStatusOrderByCreatedAtAsc(AdultVerification.Status status, Pageable pageable);

    List<AdultVerification> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<AdultVerification> findFirstByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, AdultVerification.Status status);
}
