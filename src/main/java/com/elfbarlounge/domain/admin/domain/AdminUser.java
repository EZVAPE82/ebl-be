package com.elfbarlounge.domain.admin.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 어드민 사용자.
 *
 * 보안 룰 #11: 어드민 IP 화이트리스트 + 2FA (운영 시 인프라/리버스프록시·OTP 모듈 추가)
 * - 로그인 실패 5회 시 30분 잠금
 * - MASTER / OPERATOR 2단계 권한
 */
@Getter
@Entity
@Table(name = "admin_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUser extends BaseTimeEntity {

    public static final int MAX_FAILED_LOGINS = 5;
    public static final int LOCK_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 80, nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 16, nullable = false)
    private AdminRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private AdminStatus status;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    private AdminUser(String username, String passwordHash, String name, AdminRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.status = AdminStatus.ACTIVE;
        this.failedLoginCount = 0;
    }

    public boolean isLocked() {
        return status == AdminStatus.LOCKED
                || (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now()));
    }

    public void recordFailedLogin() {
        this.failedLoginCount++;
        if (failedLoginCount >= MAX_FAILED_LOGINS) {
            this.status = AdminStatus.LOCKED;
            this.lockedUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
        }
    }

    public void recordSuccessfulLogin() {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
        this.lastLoginAt = LocalDateTime.now();
        if (status == AdminStatus.LOCKED && (lockedUntil == null || lockedUntil.isBefore(LocalDateTime.now()))) {
            this.status = AdminStatus.ACTIVE;
        }
    }

    public enum AdminRole { MASTER, OPERATOR }
    public enum AdminStatus { ACTIVE, LOCKED, DISABLED }
}
