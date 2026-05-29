package com.elfbarlounge.common.config;

import com.elfbarlounge.common.security.JwtAuthenticationFilter;
import com.elfbarlounge.common.security.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Stateless JWT 기반 인증 전제.
 * - CORS: 명시된 도메인만 허용 (와일드카드 금지)
 * - CSRF: stateless API이므로 비활성, 단 OAuth state 검증은 별도
 * - 보안 헤더: HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy
 *
 * @see docs/security-guidelines.md
 */
@Configuration
public class SecurityConfig {

    private final SecurityProperties props;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final Environment environment;

    public SecurityConfig(SecurityProperties props,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          RateLimitFilter rateLimitFilter,
                          Environment environment) {
        this.props = props;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.environment = environment;
    }

    private boolean isLocalProfile() {
        for (String p : environment.getActiveProfiles()) {
            if ("local".equals(p)) return true;
        }
        return false;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean local = isLocalProfile();
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(h -> h
                // local: H2 콘솔 사용 위해 sameOrigin / prod: deny
                .frameOptions(f -> { if (local) f.sameOrigin(); else f.deny(); })
                .contentTypeOptions(c -> {})
                .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/public/**").permitAll()
                    .requestMatchers("/api/v1/admin/auth/**").permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    // 업로드된 사진 정적 서빙 — 누구나 GET 가능 (lightbox 보기용)
                    .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll();

                if (local) {
                    // local 전용: 개발 도구
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    auth.requestMatchers("/h2-console/**").permitAll();
                }
                // prod에서는 swagger·h2 colsole 자체가 막힘 (요청 시 401)

                auth.anyRequest().authenticated();
            })
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호는 BCrypt만 사용 (rule 2)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 명시 도메인만 (rule 12 — 와일드카드 금지)
        cfg.setAllowedOrigins(Arrays.asList(props.cors().allowedOrigins().split(",")));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
