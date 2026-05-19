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

/**
 * 성인 인증 신청·승인 이력.
 *
 * 메서드:
 *  - PASS: 즉시 APPROVED (PASS Provider 응답 기반)
 *  - FOREIGN_DOC: 해외거주 외국인. 신청 시 PENDING, 어드민 검토 후 APPROVED/REJECTED.
 *
 * 보안:
 *  - ci: PASS의 본인 식별값. 컬럼 암호화 검토.
 *  - document_url: 여권 사진 S3 키. 회원 탈퇴 시 자동 파기 + 어드민 열람 시 audit log.
 */
@Getter
@Entity
@Table(name = "member_adult_verifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdultVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 16, nullable = false)
    private Method method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private Status status;

    @Column(name = "ci", length = 255)
    private String ci;

    @Column(name = "document_url", columnDefinition = "TEXT")
    private String documentUrl;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AdultVerification(Long memberId, Method method, Status status, String ci, String documentUrl) {
        this.memberId = memberId;
        this.method = method;
        this.status = status != null ? status : Status.PENDING;
        this.ci = ci;
        this.documentUrl = documentUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void approve(Long adminUserId) {
        this.status = Status.APPROVED;
        this.reviewedBy = adminUserId;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(Long adminUserId, String reason) {
        this.status = Status.REJECTED;
        this.reviewedBy = adminUserId;
        this.reviewedAt = LocalDateTime.now();
        this.rejectReason = reason;
    }

    public enum Method {
        PASS, FOREIGN_DOC
    }

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
