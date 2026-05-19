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
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
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
}
