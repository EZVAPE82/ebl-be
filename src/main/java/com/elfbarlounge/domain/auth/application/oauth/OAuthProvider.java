package com.elfbarlounge.domain.auth.application.oauth;

/**
 * 소셜 로그인 Provider. 카카오/구글 두 가지.
 * (네이버는 전자담배 카테고리 정책상 제외 — v1.5 1.1 사유 명시)
 *
 * 구현체는 도급인이 콘솔 키 발급 후 채울 것:
 * - KakaoOAuthProvider
 * - GoogleOAuthProvider
 */
public interface OAuthProvider {

    Provider getProvider();

    /**
     * Authorization code를 access token으로 교환하고 사용자 프로필을 가져온다.
     */
    OAuthUserInfo fetchUserInfo(String authorizationCode, String redirectUri);

    enum Provider {
        KAKAO, GOOGLE
    }

    record OAuthUserInfo(
            Provider provider,
            String providerUserId,
            String email,
            String name
    ) {}
}
