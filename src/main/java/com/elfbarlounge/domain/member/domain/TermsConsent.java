package com.elfbarlounge.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "member_terms_consents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_code", length = 40, nullable = false)
    private TermCode termCode;

    @Column(name = "term_version", length = 16, nullable = false)
    private String termVersion;

    @Column(name = "agreed", nullable = false)
    private boolean agreed;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;

    @Builder
    private TermsConsent(Long memberId, TermCode termCode, String termVersion, boolean agreed) {
        this.memberId = memberId;
        this.termCode = termCode;
        this.termVersion = termVersion;
        this.agreed = agreed;
        this.agreedAt = LocalDateTime.now();
    }

    public enum TermCode {
        TOS, PRIVACY, YOUTH, MARKETING
    }
}
