package com.elfbarlounge.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 민감 endpoint별 rate limit 적용.
 *
 * 룰:
 *  - POST /api/v1/auth/login              : IP당 10회/분
 *  - POST /api/v1/auth/signup             : IP당 5회/분
 *  - POST /api/v1/auth/find-email         : IP당 10회/분
 *  - POST /api/v1/auth/password-reset/*   : IP당 5회/분
 *  - POST /api/v1/admin/auth/login        : IP당 10회/분  (어드민 자체에 5회 잠금이 있지만 다른 계정명 무차별 차단용)
 *  - POST /api/v1/orders/checkout         : IP당 20회/분  (정상 사용은 1~2회/주문)
 *
 * 429 응답: { code, message, retryAfterSec }
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter limiter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<Rule> RULES = List.of(
            new Rule("POST", "/api/v1/auth/login", 10, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/auth/signup", 5, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/auth/find-email", 10, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/auth/password-reset/request", 5, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/auth/password-reset/confirm", 10, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/admin/auth/login", 10, Duration.ofMinutes(1)),
            new Rule("POST", "/api/v1/orders/checkout", 20, Duration.ofMinutes(1))
    );

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String method = req.getMethod();
        String path = req.getRequestURI();

        for (Rule r : RULES) {
            if (r.method().equalsIgnoreCase(method) && r.path().equals(path)) {
                String key = path + "::" + clientIp(req);
                if (!limiter.tryConsume(key, r.capacity(), r.window())) {
                    write429(res);
                    return;
                }
                break;
            }
        }
        chain.doFilter(req, res);
    }

    private void write429(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        Map<String, Object> body = Map.of(
                "code", "RATE_LIMITED",
                "message", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
                "traceId", UUID.randomUUID().toString().substring(0, 8),
                "timestamp", Instant.now().toString()
        );
        objectMapper.writeValue(res.getWriter(), body);
    }

    private String clientIp(HttpServletRequest req) {
        // Spring forward-headers-strategy: framework 가 X-Forwarded-For를 처리하므로 getRemoteAddr만 사용
        return req.getRemoteAddr();
    }

    private record Rule(String method, String path, int capacity, Duration window) {}
}
