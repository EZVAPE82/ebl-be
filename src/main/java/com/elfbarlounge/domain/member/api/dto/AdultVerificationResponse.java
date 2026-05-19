package com.elfbarlounge.domain.member.api.dto;

import com.elfbarlounge.domain.member.domain.AdultVerification;

import java.time.LocalDateTime;

/**
 * 어드민 응답 DTO.
 *
 * 보안 (rule 10):
 *  - 여권 사진의 S3 키 자체는 응답에서 마스킹 (앞 6자만).
 *  - 실제 서류 열람은 별도 /admin/adult-verifications/{id}/document endpoint로
 *    presigned URL을 한 번만 발급하고 audit log 기록 (TODO 별도 구현).
 *  - documentS3Key 평문 노출은 어드민 자체 PC 도난·세션 탈취 시 영구 다운로드 링크 유출 위험.
 */
public record AdultVerificationResponse(
        Long id,
        Long memberId,
        AdultVerification.Method method,
        AdultVerification.Status status,
        String documentMasked,
        boolean hasDocument,
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
                maskS3Key(v.getDocumentUrl()),
                v.getDocumentUrl() != null,
                v.getCreatedAt(),
                v.getReviewedAt(),
                v.getRejectReason()
        );
    }

    private static String maskS3Key(String s) {
        if (s == null || s.isBlank()) return null;
        if (s.length() <= 8) return "****";
        return s.substring(0, 6) + "****";
    }
}
