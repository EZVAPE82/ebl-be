package com.elfbarlounge.domain.auth.application.oauth;

/**
 * 소셜 로그인 Provider.
 *
 * 활성 (현재):
 *  - KAKAO
 *  - GOOGLE
 *
 * 보류 (무니코틴 전환 시 활성):
 *  - NAVER (네이버 검수 정책상 현재 자사몰엔 사용 불가, 무니코틴 카테고리로 전환 시
 *           toggle app.integrations.naver-login.enabled=true 로 즉시 활성)
 *
 * 구현체는 도급인이 콘솔 키 발급 후 채울 것:
 *  - KakaoOAuthProvider, GoogleOAuthProvider, NaverOAuthProvider
 */
public interface OAuthProvider {

    Provider getProvider();

    /**
     * Authorization code를 access token으로 교환하고 사용자 프로필을 가져온다.
     */
    OAuthUserInfo fetchUserInfo(String authorizationCode, String redirectUri);

    enum Provider {
        KAKAO, GOOGLE, NAVER
    }

    record OAuthUserInfo(
            Provider provider,
            String providerUserId,
            String email,
            String name
    ) {}
}
