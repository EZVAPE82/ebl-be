package com.elfbarlounge.common.security;

/**
 * SecurityContext에 저장되는 인증 주체.
 */
public record AuthPrincipal(Long memberId, String role) {
}
