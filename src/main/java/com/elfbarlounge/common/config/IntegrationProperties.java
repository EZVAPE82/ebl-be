package com.elfbarlounge.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 외부 연동 활성화 토글.
 *
 * 운영 정책상 비활성인 항목들 (네이버 로그인·오픈마켓 직접 API)을
 * 무니코틴 전환 시점에 환경변수만 ON으로 바꾸면 즉시 활성화되도록 분리.
 *
 * 활성화 절차:
 *  1. 도급인이 API 키 발급 (네이버 디벨로퍼스 / 네이버 커머스 API / 11번가 OpenAPI)
 *  2. 환경변수에 키 + enabled=true 주입
 *  3. 재배포 — Stub → 실 구현체로 자동 교체 (Bean 분기)
 */
@ConfigurationProperties(prefix = "app.integrations")
public record IntegrationProperties(
        ToggleWithKey naverLogin,
        ToggleWithKey naverCommerce,
        ToggleWithKey elevenOpenapi
) {
    public IntegrationProperties {
        if (naverLogin == null) naverLogin = ToggleWithKey.disabled();
        if (naverCommerce == null) naverCommerce = ToggleWithKey.disabled();
        if (elevenOpenapi == null) elevenOpenapi = ToggleWithKey.disabled();
    }

    public record ToggleWithKey(
            boolean enabled,
            String clientId,
            String clientSecret
    ) {
        public static ToggleWithKey disabled() {
            return new ToggleWithKey(false, null, null);
        }
    }
}
