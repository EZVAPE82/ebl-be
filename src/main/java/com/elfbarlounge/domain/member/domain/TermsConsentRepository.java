package com.elfbarlounge.domain.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsConsentRepository extends JpaRepository<TermsConsent, Long> {
    List<TermsConsent> findByMemberId(Long memberId);
}
