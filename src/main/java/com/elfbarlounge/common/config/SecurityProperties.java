package com.elfbarlounge.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * app.security.* 설정 바인딩.
 * 시크릿은 .env / 환경변수 / Secrets Manager에서 주입.
 */
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        Jwt jwt,
        Cors cors
) {
    public record Jwt(
            String secret,
            int accessExpiryMin,
            int refreshExpiryDays
    ) {}

    public record Cors(
            String allowedOrigins
    ) {}
}
