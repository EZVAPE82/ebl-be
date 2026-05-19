package com.elfbarlounge.domain.auth.api.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSec
) {
    public static TokenResponse of(String access, String refresh, long expiresInSec) {
        return new TokenResponse(access, refresh, "Bearer", expiresInSec);
    }
}
