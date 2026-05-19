package com.elfbarlounge.common.security;

/**
 * SecurityContext에 저장되는 인증 주체.
 *
 * id 의미:
 *  - role=USER  → members.id
 *  - role=ADMIN → admin_users.id  (다른 PK 공간이므로 절대 혼용 X)
 *
 * 메서드:
 *  - memberId(): role=USER 컨텍스트에서만 호출
 *  - adminUserId(): role=ADMIN 컨텍스트에서만 호출
 * 두 메서드 모두 호출 전에 role 검증 또는 컨트롤러 단의 권한 매처로 보호된 endpoint에서만 사용할 것.
 */
public record AuthPrincipal(Long id, String role) {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isUser() {
        return ROLE_USER.equals(role);
    }

    /** role=USER 컨텍스트 전용. */
    public Long memberId() {
        if (!isUser()) {
            throw new IllegalStateException(
                "memberId() is for USER role only. Current role=" + role);
        }
        return id;
    }

    /** role=ADMIN 컨텍스트 전용. */
    public Long adminUserId() {
        if (!isAdmin()) {
            throw new IllegalStateException(
                "adminUserId() is for ADMIN role only. Current role=" + role);
        }
        return id;
    }
}
