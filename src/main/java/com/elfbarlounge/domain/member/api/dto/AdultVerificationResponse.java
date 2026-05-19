package com.elfbarlounge.domain.member.api.dto;

import com.elfbarlounge.domain.member.domain.AdultVerification;

import java.time.LocalDateTime;

public record AdultVerificationResponse(
        Long id,
        Long memberId,
        AdultVerification.Method method,
        AdultVerification.Status status,
        String documentS3Key,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        String rejectReason
) {
    public static AdultVerificationResponse from(AdultVerification v) {
        return new AdultVerificationResponse(
                v.getId(),
                v.getMemberId(),
                v.getMethod(),
                v.getStatus(),
                v.getDocumentUrl(),
                v.getCreatedAt(),
                v.getReviewedAt(),
                v.getRejectReason()
        );
    }
}
