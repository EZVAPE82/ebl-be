package com.elfbarlounge.common.security;

import com.elfbarlounge.common.config.SecurityProperties;
import com.elfbarlounge.common.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

/**
 * JWT 발급·검증.
 *
 * 보안 룰:
 * - rule 7: secret 256-bit 이상, refresh rotation
 * - access token: 짧게 (기본 30분), claim 최소
 * - refresh token: 길게 (기본 14일), DB에 해시 저장
 */
@Slf4j
@Component
public class JwtService {

    private final SecretKey key;
    private final Duration accessExpiry;
    private final Duration refreshExpiry;

    public JwtService(SecurityProperties props) {
        byte[] secretBytes = props.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 256-bit (32 bytes). " +
                "현재 길이: " + secretBytes.length + " bytes. " +
                "환경변수 JWT_SECRET 확인.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.accessExpiry = Duration.ofMinutes(props.jwt().accessExpiryMin());
        this.refreshExpiry = Duration.ofDays(props.jwt().refreshExpiryDays());
    }

    public String issueAccessToken(Long memberId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("role", role)
                .claim("typ", "access")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiry.toMillis()))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public String issueRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("typ", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiry.toMillis()))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw ApiException.unauthorized("TOKEN_EXPIRED", "토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            // 내부 사유는 서버 로그에만 (vibe 함정 #2)
            log.debug("JWT parse failed: {}", e.getMessage());
            throw ApiException.unauthorized("TOKEN_INVALID", "유효하지 않은 토큰입니다.");
        }
    }

    public Duration getRefreshExpiry() {
        return refreshExpiry;
    }

    /** Refresh token은 평문으로 DB 저장 X. SHA-256 해시 저장. */
    public String hashRefreshToken(String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
