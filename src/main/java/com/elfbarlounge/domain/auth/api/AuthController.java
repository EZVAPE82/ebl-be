package com.elfbarlounge.domain.auth.api;

import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.common.security.CookieUtil;
import com.elfbarlounge.common.security.JwtService;
import com.elfbarlounge.domain.auth.api.dto.LoginRequest;
import com.elfbarlounge.domain.auth.api.dto.RefreshRequest;
import com.elfbarlounge.domain.auth.api.dto.SignupRequest;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import com.elfbarlounge.domain.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
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
    private final JwtService jwtService;
    private final Environment environment;

    @Operation(summary = "회원가입 (자체)")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@jakarta.validation.Valid @RequestBody SignupRequest req) {
        Long id = authService.signup(req);
        return ResponseEntity
                .created(URI.create("/api/v1/members/" + id))
                .body(Map.of("memberId", id, "status", "PENDING",
                        "message", "회원가입이 완료되었습니다. 성인 인증 후 이용 가능합니다."));
    }

    @Operation(summary = "로그인 (자체) — httpOnly 쿠키 + JSON 둘 다 발급")
    @PostMapping("/login")
    public TokenResponse login(@jakarta.validation.Valid @RequestBody LoginRequest req,
                               HttpServletRequest http,
                               HttpServletResponse res) {
        TokenResponse t = authService.login(req, http.getHeader("User-Agent"), clientIp(http));
        writeAuthCookies(res, t);
        return t;
    }

    @Operation(summary = "Access Token 갱신 (refresh rotation)")
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody(required = false) RefreshRequest req,
                                 HttpServletRequest http,
                                 HttpServletResponse res) {
        // Body 우선, 없으면 쿠키 (httpOnly 전환 점진 지원)
        String rt = (req != null && req.refreshToken() != null) ? req.refreshToken()
                : CookieUtil.readCookie(http, CookieUtil.COOKIE_REFRESH);
        if (rt == null || rt.isBlank()) {
            throw com.elfbarlounge.common.exception.ApiException.unauthorized(
                    "TOKEN_MISSING", "refresh token이 없습니다.");
        }
        TokenResponse t = authService.refresh(rt, http.getHeader("User-Agent"), clientIp(http));
        writeAuthCookies(res, t);
        return t;
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthPrincipal principal,
                                       HttpServletResponse res) {
        if (principal != null && principal.isUser()) {
            authService.logout(principal.memberId());
        }
        // 인증 안 됐어도 쿠키는 만료시켜야 함
        boolean secure = isProd();
        CookieUtil.expireCookie(res, CookieUtil.COOKIE_ACCESS, secure);
        CookieUtil.expireCookie(res, CookieUtil.COOKIE_REFRESH, secure);
        return ResponseEntity.noContent().build();
    }

    private void writeAuthCookies(HttpServletResponse res, TokenResponse t) {
        boolean secure = isProd();
        long accessSec = jwtService.getAccessExpiry().toSeconds();
        long refreshSec = jwtService.getRefreshExpiry().toSeconds();
        CookieUtil.setSecureCookie(res, CookieUtil.COOKIE_ACCESS, t.accessToken(), accessSec, secure);
        CookieUtil.setSecureCookie(res, CookieUtil.COOKIE_REFRESH, t.refreshToken(), refreshSec, secure);
    }

    private boolean isProd() {
        for (String p : environment.getActiveProfiles()) {
            if ("local".equals(p)) return false;
        }
        return true;
    }

    /**
     * 클라이언트 IP — application.yml의 forward-headers-strategy: framework 가
     * 신뢰 프록시(Nginx) 통과한 X-Forwarded-For를 자동 처리하므로 getRemoteAddr만 사용.
     * 헤더 직접 파싱은 스푸핑 위험 (rule 11).
     */
    private String clientIp(HttpServletRequest req) {
        return req.getRemoteAddr();
    }
}
