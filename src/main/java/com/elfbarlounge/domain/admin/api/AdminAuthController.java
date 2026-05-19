package com.elfbarlounge.domain.admin.api;

import com.elfbarlounge.domain.admin.api.dto.AdminLoginRequest;
import com.elfbarlounge.domain.admin.application.AdminAuthService;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "[Admin] 로그인 — JWT role=ADMIN")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody AdminLoginRequest req) {
        return adminAuthService.login(req);
    }
}
