package com.elfbarlounge.domain.member.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.member.api.dto.AdultVerificationResponse;
import com.elfbarlounge.domain.member.api.dto.ForeignVerificationRequest;
import com.elfbarlounge.domain.member.application.AdultVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "AdultVerification")
@RestController
@RequiredArgsConstructor
public class AdultVerificationController {

    private final AdultVerificationService service;

    @Operation(summary = "해외거주 외국인 성인인증 신청 (여권 업로드 후 S3 키 전달)")
    @PostMapping("/api/v1/members/me/adult-verifications/foreign")
    public ResponseEntity<Map<String, Object>> submit(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody ForeignVerificationRequest req
    ) {
        if (principal == null) {
            throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        }
        Long id = service.submitForeignVerification(principal.memberId(), req.documentS3Key());
        return ResponseEntity.created(URI.create("/api/v1/admin/adult-verifications/" + id))
                .body(Map.of("id", id, "status", "PENDING"));
    }

    // ----------- 어드민 영역 -----------

    @Operation(summary = "[Admin] PENDING 신청 목록")
    @GetMapping("/api/v1/admin/adult-verifications")
    public Page<AdultVerificationResponse> listPending(Pageable pageable) {
        return service.listPending(pageable).map(AdultVerificationResponse::from);
    }

    @Operation(summary = "[Admin] 승인")
    @PostMapping("/api/v1/admin/adult-verifications/{id}/approve")
    public ResponseEntity<Void> approve(
            @AuthenticationPrincipal AuthPrincipal admin,
            @PathVariable Long id
    ) {
        service.approve(id, admin.memberId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[Admin] 반려")
    @PostMapping("/api/v1/admin/adult-verifications/{id}/reject")
    public ResponseEntity<Void> reject(
            @AuthenticationPrincipal AuthPrincipal admin,
            @PathVariable Long id,
            @Valid @RequestBody RejectRequest req
    ) {
        service.reject(id, admin.memberId(), req.reason());
        return ResponseEntity.noContent().build();
    }

    public record RejectRequest(@NotBlank @Size(max = 500) String reason) {}
}
