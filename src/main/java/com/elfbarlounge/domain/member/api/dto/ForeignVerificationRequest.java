package com.elfbarlounge.domain.member.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 해외거주 외국인 성인 인증 신청.
 *
 * documentS3Key: 사전에 presigned URL로 S3에 업로드된 여권 사진의 키.
 * (파일 직접 multipart 업로드보다 보안 측면에서 S3 분리가 안전 — rule 13)
 */
public record ForeignVerificationRequest(
        @NotBlank
        @Size(max = 500)
        String documentS3Key
) {
}
