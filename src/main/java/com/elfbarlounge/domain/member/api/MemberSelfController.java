package com.elfbarlounge.domain.member.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.member.application.MemberSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "MemberSelf")
@RestController
@RequiredArgsConstructor
public class MemberSelfController {

    private final MemberSelfService memberSelfService;
    private final Environment environment;

    @Operation(summary = "아이디(이메일) 찾기 — 휴대폰 번호로 조회, 마스킹 반환")
    @PostMapping("/api/v1/auth/find-email")
    public Map<String, Object> findEmail(@Valid @RequestBody FindEmailRequest req) {
        Map<String, Object> body = new HashMap<>();
        memberSelfService.findEmailByPhone(req.phone()).ifPresent(masked -> body.put("emailMasked", masked));
        // 존재 여부 노출 방지: 항상 200, found=true/false
        body.put("found", body.containsKey("emailMasked"));
        return body;
    }

    @Operation(summary = "비밀번호 재설정 토큰 발급 — 이메일 존재 여부 미노출")
    @PostMapping("/api/v1/auth/password-reset/request")
    public Map<String, Object> requestReset(@Valid @RequestBody RequestResetRequest req) {
        // 항상 동일 응답: 보안상 토큰 발급 성공 여부 미노출
        // 운영에서는 이메일/SMS로 발송. local 프로파일에서만 devToken 노출 (개발 편의).
        Map<String, Object> body = new HashMap<>();
        var tokenOpt = memberSelfService.issueResetToken(req.email());
        if (isLocalProfile()) {
            tokenOpt.ifPresent(t -> body.put("devToken", t));
        }
        body.put("message", "발송 요청이 접수되었습니다.");
        return body;
    }

    private boolean isLocalProfile() {
        for (String p : environment.getActiveProfiles()) {
            if ("local".equals(p)) return true;
        }
        return false;
    }

    @Operation(summary = "비밀번호 재설정 실행")
    @PostMapping("/api/v1/auth/password-reset/confirm")
    public ResponseEntity<Void> confirmReset(@Valid @RequestBody ConfirmResetRequest req) {
        memberSelfService.resetPassword(req.token(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

    // ----- 인증 필요 (마이페이지) -----

    @Operation(summary = "비밀번호 변경 (인증 후)")
    @PostMapping("/api/v1/members/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        require(principal);
        memberSelfService.changePassword(principal.memberId(), req.currentPassword(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원정보 수정 (마케팅 수신동의 등)")
    @PutMapping("/api/v1/members/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        require(principal);
        memberSelfService.updateProfile(principal.memberId(),
                new MemberSelfService.UpdateProfile(req.marketingEmailAgreed(), req.marketingSmsAgreed()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴 (개인정보 즉시 익명화)")
    @DeleteMapping("/api/v1/members/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal AuthPrincipal principal) {
        require(principal);
        memberSelfService.withdraw(principal.memberId());
        return ResponseEntity.noContent().build();
    }

    // ===== DTO =====

    public record FindEmailRequest(
            @NotBlank @Pattern(regexp = "^[0-9+\\-]{8,20}$") String phone
    ) {}

    public record RequestResetRequest(
            @Email @NotBlank String email
    ) {}

    public record ConfirmResetRequest(
            @NotBlank @Size(max = 200) String token,
            @NotBlank
            @Size(min = 10, max = 100)
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{10,}$",
                    message = "비밀번호는 영문·숫자·특수문자를 각 1자 이상 포함해야 합니다."
            )
            String newPassword
    ) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank
            @Size(min = 10, max = 100)
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{10,}$"
            )
            String newPassword
    ) {}

    public record UpdateProfileRequest(
            boolean marketingEmailAgreed,
            boolean marketingSmsAgreed
    ) {}

    private void require(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
    }
}
