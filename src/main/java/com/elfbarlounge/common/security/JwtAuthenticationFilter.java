package com.elfbarlounge.common.security;

import com.elfbarlounge.common.exception.ApiException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 토큰 추출 우선순위:
        //  1) Authorization 헤더 (기존 SPA·모바일 호환)
        //  2) httpOnly 쿠키 (eb_at 또는 eb_aat — XSS 방어, P0-8 점진 전환)
        String token = extractToken(request);
        if (token != null) {
            try {
                Claims claims = jwtService.parse(token);
                if (!"access".equals(claims.get("typ", String.class))) {
                    // refresh token으로는 일반 API 접근 불가
                    chain.doFilter(request, response);
                    return;
                }
                Long memberId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);
                if (role == null) {
                    role = "USER";
                }

                AuthPrincipal principal = new AuthPrincipal(memberId, role);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ApiException e) {
                // 토큰 만료/오류는 인증 안 된 상태로 통과 → 보호된 endpoint에서 401 처리
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        // 헤더 미존재 시 쿠키 fallback (httpOnly)
        String cookie = CookieUtil.readCookie(request, CookieUtil.COOKIE_ACCESS);
        if (cookie != null && !cookie.isBlank()) return cookie;
        return CookieUtil.readCookie(request, CookieUtil.COOKIE_ADMIN_ACCESS);
    }
}
