package com.elfbarlounge.common.config;

import com.elfbarlounge.domain.admin.domain.AdminUser;
import com.elfbarlounge.domain.admin.domain.AdminUserRepository;
import com.elfbarlounge.domain.settings.application.PolicySettingsService;
import com.elfbarlounge.domain.settings.domain.PolicySetting;
import com.elfbarlounge.domain.settings.domain.PolicySettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private final PolicySettingRepository policySettingRepository;
    private final PolicySettingsService policySettingsService;
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
            // 보안 룰 #2/9: 비밀번호 평문 로그 금지. 자격 증명은 README/admin-manual 참조.
            log.warn("[local-seed] 어드민 마스터 계정 'admin' 생성됨 (자격 증명은 README 참조)");
        }

        if (policySettingRepository.count() == 0) {
            List<Map.Entry<String[], String>> defaults = List.of(
                    Map.entry(new String[]{"free_shipping_threshold", "50000"}, "무료배송 기준 금액"),
                    Map.entry(new String[]{"point_min_use_amount", "1000"}, "적립금 최소 사용 금액"),
                    Map.entry(new String[]{"point_min_balance", "1000"}, "적립금 사용 최소 보유 금액"),
                    Map.entry(new String[]{"point_max_use_rate", "50"}, "적립금 최대 사용 비율(%)"),
                    Map.entry(new String[]{"return_shipping_fee", "3000"}, "반송 택배비"),
                    Map.entry(new String[]{"coupon_point_concurrent", "true"}, "쿠폰·적립금 중복 허용"),
                    Map.entry(new String[]{"shipping_fee_default", "3000"}, "기본 배송비")
            );
            defaults.forEach(e -> policySettingRepository.save(
                    new PolicySetting(e.getKey()[0], e.getKey()[1], e.getValue())));
            log.info("[local-seed] PolicySetting 7 entries seeded");
            policySettingsService.preload();
        }
    }
}
