package com.elfbarlounge.domain.auth.api;

import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.auth.api.dto.LoginRequest;
import com.elfbarlounge.domain.auth.api.dto.RefreshRequest;
import com.elfbarlounge.domain.auth.api.dto.SignupRequest;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import com.elfbarlounge.domain.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입 (자체)")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest req) {
        Long id = authService.signup(req);
        return ResponseEntity
                .created(URI.create("/api/v1/members/" + id))
                .body(Map.of("memberId", id, "status", "PENDING",
                        "message", "회원가입이 완료되었습니다. 성인 인증 후 이용 가능합니다."));
    }

    @Operation(summary = "로그인 (자체)")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return authService.login(req, http.getHeader("User-Agent"), clientIp(http));
    }

    @Operation(summary = "Access Token 갱신 (refresh rotation)")
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req, HttpServletRequest http) {
        return authService.refresh(req.refreshToken(), http.getHeader("User-Agent"), clientIp(http));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthPrincipal principal) {
        if (principal != null) {
            authService.logout(principal.memberId());
        }
        return ResponseEntity.noContent().build();
    }

    private String clientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
