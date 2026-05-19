package com.elfbarlounge.domain.member.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.auth.domain.RefreshTokenRepository;
import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberRepository;
import com.elfbarlounge.domain.member.domain.PasswordResetToken;
import com.elfbarlounge.domain.member.domain.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * 회원 본인 자기관리: 정보 수정, 비밀번호 변경, 탈퇴, 아이디 찾기, 비밀번호 재설정.
 *
 * 보안:
 *  - 아이디 찾기 응답은 이메일 마스킹 (te**@x.com)
 *  - 비밀번호 재설정 토큰은 평문 저장 X (SHA-256 해시)
 *  - 토큰 만료 30분, 1회 사용
 *  - 비밀번호 변경 시 모든 refresh token 회수
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSelfService {

    private static final int RESET_TOKEN_EXPIRY_MIN = 30;

    private final MemberRepository memberRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    /** 휴대폰 번호로 가입된 이메일을 마스킹 반환. 없으면 빈 Optional (메시지는 항상 동일하게). */
    @Transactional(readOnly = true)
    public Optional<String> findEmailByPhone(String phone) {
        // V10에서 phone 인덱스 추가됨. 운영에선 phone 컬럼 암호화도 검토.
        return memberRepository.findByPhoneAndStatusNot(
                phone, com.elfbarlounge.domain.member.domain.MemberStatus.WITHDRAWN
        ).map(m -> maskEmail(m.getEmail()));
    }

    /** 비밀번호 재설정 토큰 발급. 이메일 존재 여부 노출 X — 항상 200. */
    @Transactional
    public Optional<String> issueResetToken(String email) {
        return memberRepository.findByEmail(email)
                .filter(m -> !m.isWithdrawn())
                .map(m -> {
                    byte[] raw = new byte[32];
                    secureRandom.nextBytes(raw);
                    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
                    resetTokenRepository.save(PasswordResetToken.builder()
                            .memberId(m.getId())
                            .tokenHash(hash(token))
                            .expiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MIN))
                            .build());
                    return token;
                });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken t = resetTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> ApiException.badRequest("TOKEN_INVALID", "유효하지 않은 토큰입니다."));
        if (!t.isActive()) {
            throw ApiException.badRequest("TOKEN_EXPIRED", "만료되었거나 이미 사용된 토큰입니다.");
        }
        Member m = memberRepository.findById(t.getMemberId())
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        changePasswordHash(m, newPassword);
        t.markUsed();
        refreshTokenRepository.revokeAllByMemberId(m.getId());
    }

    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        if (m.getPasswordHash() == null || !passwordEncoder.matches(currentPassword, m.getPasswordHash())) {
            throw ApiException.badRequest("AUTH_FAILED", "현재 비밀번호가 올바르지 않습니다.");
        }
        changePasswordHash(m, newPassword);
        refreshTokenRepository.revokeAllByMemberId(memberId);
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfile req) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        // Member 엔티티에 setter가 없으므로 새 메서드 추가 검토 — MVP에서는 reflection 회피, 새 update 메서드 추가가 안전.
        // 지금은 marketingEmailAgreed/marketingSmsAgreed만 변경 가능하다고 가정. (이름·전화 변경 시 별도 검증·인증 필요)
        // 여기선 단순화: Member에 changeMarketingAgreement 등 메서드를 추가 (도메인 룰 보호).
        m.updateMarketingAgreement(req.marketingEmailAgreed(), req.marketingSmsAgreed());
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        m.withdraw();
        refreshTokenRepository.revokeAllByMemberId(memberId);
        log.info("Member withdrawn: id={}", memberId);
    }

    public record UpdateProfile(boolean marketingEmailAgreed, boolean marketingSmsAgreed) {}

    private void changePasswordHash(Member m, String raw) {
        if (raw == null || raw.length() < 10) {
            throw ApiException.badRequest("PASSWORD_WEAK", "비밀번호는 10자 이상이어야 합니다.");
        }
        // Member 엔티티에 changePassword 추가
        m.changePassword(passwordEncoder.encode(raw));
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "***";
        int at = email.indexOf('@');
        if (at < 3) return "***" + (at >= 0 ? email.substring(at) : "");
        return email.substring(0, 2) + "***" + email.substring(at);
    }
}
