package com.elfbarlounge.domain.admin.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.JwtService;
import com.elfbarlounge.domain.admin.api.dto.AdminLoginRequest;
import com.elfbarlounge.domain.admin.domain.AdminUser;
import com.elfbarlounge.domain.admin.domain.AdminUserRepository;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse login(AdminLoginRequest req) {
        AdminUser admin = adminUserRepository.findByUsername(req.username())
                .orElseThrow(() -> ApiException.unauthorized("AUTH_FAILED", "아이디 또는 비밀번호가 올바르지 않습니다."));

        if (admin.getStatus() == AdminUser.AdminStatus.DISABLED) {
            throw ApiException.forbidden("ADMIN_DISABLED", "비활성화된 계정입니다.");
        }
        if (admin.isLocked()) {
            throw ApiException.forbidden("ADMIN_LOCKED",
                    "로그인이 일시 잠금되었습니다. 잠시 후 다시 시도하세요.");
        }
        if (!passwordEncoder.matches(req.password(), admin.getPasswordHash())) {
            admin.recordFailedLogin();
            throw ApiException.unauthorized("AUTH_FAILED", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        admin.recordSuccessfulLogin();
        String access = jwtService.issueAccessToken(admin.getId(), "ADMIN");
        return TokenResponse.of(access, "", Duration.ofMinutes(15).toSeconds());
    }
}
