package com.elfbarlounge.domain.auth.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.JwtService;
import com.elfbarlounge.domain.auth.api.dto.LoginRequest;
import com.elfbarlounge.domain.auth.api.dto.SignupRequest;
import com.elfbarlounge.domain.auth.api.dto.TokenResponse;
import com.elfbarlounge.domain.auth.domain.RefreshToken;
import com.elfbarlounge.domain.auth.domain.RefreshTokenRepository;
import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberRepository;
import com.elfbarlounge.domain.member.domain.MemberType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 회원가입·로그인·토큰 갱신.
 *
 * 보안:
 * - 비밀번호는 BCrypt (rule 2)
 * - Refresh token rotation (rule 7)
 * - 가입 직후 status=PENDING, 성인인증 완료 후 ACTIVE (성인인증은 별도 도메인)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public Long signup(SignupRequest req) {
        if (memberRepository.existsByEmail(req.email())) {
            throw ApiException.conflict("EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .phone(req.phone())
                .birthDate(req.birthDate())
                .gender(req.gender())
                .memberType(req.memberType() != null ? req.memberType() : MemberType.KOREAN)
                .joinChannel(req.joinChannel())
                .marketingEmailAgreed(req.marketingEmailAgreed())
                .marketingSmsAgreed(req.marketingSmsAgreed())
                .build();

        Member saved = memberRepository.save(member);
        return saved.getId();
    }

    @Transactional
    public TokenResponse login(LoginRequest req, String userAgent, String ipAddress) {
        Member member = memberRepository.findByEmail(req.email())
                .orElseThrow(() -> ApiException.unauthorized("AUTH_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (member.isWithdrawn()) {
            throw ApiException.unauthorized("AUTH_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (member.getPasswordHash() == null
                || !passwordEncoder.matches(req.password(), member.getPasswordHash())) {
            // 메시지는 항상 동일하게 (이메일 존재 여부 노출 방지)
            throw ApiException.unauthorized("AUTH_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        member.recordLogin();
        return issueTokens(member.getId(), userAgent, ipAddress);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken, String userAgent, String ipAddress) {
        Claims claims = jwtService.parse(rawRefreshToken);
        if (!"refresh".equals(claims.get("typ", String.class))) {
            throw ApiException.unauthorized("TOKEN_INVALID", "유효하지 않은 토큰입니다.");
        }
        Long memberId = Long.valueOf(claims.getSubject());

        String hash = jwtService.hashRefreshToken(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> ApiException.unauthorized("TOKEN_INVALID", "유효하지 않은 토큰입니다."));
        if (!stored.isActive() || !stored.getMemberId().equals(memberId)) {
            // 도용 의심 시 해당 회원 모든 토큰 회수
            refreshTokenRepository.revokeAllByMemberId(memberId);
            throw ApiException.unauthorized("TOKEN_INVALID", "유효하지 않은 토큰입니다.");
        }

        // Rotation: 기존 폐기, 새로 발급
        stored.revoke();
        return issueTokens(memberId, userAgent, ipAddress);
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.revokeAllByMemberId(memberId);
    }

    private TokenResponse issueTokens(Long memberId, String userAgent, String ipAddress) {
        String access = jwtService.issueAccessToken(memberId, "USER");
        String refresh = jwtService.issueRefreshToken(memberId);
        Duration expiry = jwtService.getRefreshExpiry();

        RefreshToken record = RefreshToken.builder()
                .memberId(memberId)
                .tokenHash(jwtService.hashRefreshToken(refresh))
                .userAgent(truncate(userAgent, 255))
                .ipAddress(truncate(ipAddress, 64))
                .expiresAt(LocalDateTime.now().plus(expiry))
                .build();
        refreshTokenRepository.save(record);

        long accessExpirySec = Duration.ofMinutes(30).toSeconds(); // application.yml과 일치 필요
        return TokenResponse.of(access, refresh, accessExpirySec);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
