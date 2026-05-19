package com.elfbarlounge.domain.admin.api;

import com.elfbarlounge.common.security.CookieUtil;
import com.elfbarlounge.common.security.JwtService;
import com.elfbarlounge.domain.admin.api.dto.AdminLoginRequest;
import com.elfbarlounge.domain.admin.application.AdminAuthService;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AdminAuth")
@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final JwtService jwtService;
    private final Environment environment;

    @Operation(summary = "[Admin] 로그인 — JWT role=ADMIN + httpOnly 쿠키 발급")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody AdminLoginRequest req,
                               HttpServletResponse res) {
        TokenResponse t = adminAuthService.login(req);
        // 어드민은 refresh 없이 짧은 access(15분)만. 쿠키도 access만.
        CookieUtil.setSecureCookie(
                res, CookieUtil.COOKIE_ADMIN_ACCESS,
                t.accessToken(),
                jwtService.getAccessExpiry().toSeconds(),
                isProd()
        );
        return t;
    }

    @Operation(summary = "[Admin] 로그아웃 — 쿠키 만료")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        CookieUtil.expireCookie(res, CookieUtil.COOKIE_ADMIN_ACCESS, isProd());
        return ResponseEntity.noContent().build();
    }

    private boolean isProd() {
        for (String p : environment.getActiveProfiles()) {
            if ("local".equals(p)) return false;
        }
        return true;
    }
}
