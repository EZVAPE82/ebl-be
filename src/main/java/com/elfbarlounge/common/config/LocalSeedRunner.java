package com.elfbarlounge.common.config;

import com.elfbarlounge.domain.admin.domain.AdminUser;
import com.elfbarlounge.domain.admin.domain.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 로컬 개발 시드 데이터.
 * 운영(prod)에서는 동작 X (@Profile("local")).
 */
@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class LocalSeedRunner implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!adminUserRepository.existsByUsername("admin")) {
            adminUserRepository.save(AdminUser.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("LocalAdmin1!"))
                    .name("로컬 마스터")
                    .role(AdminUser.AdminRole.MASTER)
                    .build());
            log.warn("[local-seed] 어드민 마스터 생성: admin / LocalAdmin1!  (운영에서 절대 사용 금지)");
        }
    }
}
