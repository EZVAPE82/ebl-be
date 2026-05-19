package com.elfbarlounge.domain.auth.application.oauth;

import com.elfbarlounge.common.exception.ApiException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 도급인 키 미수령 상태에서의 임시 구현.
 * 실제 호출 시 501 Not Implemented 반환.
 *
 * 도급인 콘솔 등록 완료 후 카카오/구글 각각 별도 구현체로 교체.
 * 실연동 시 OAuth state 파라미터 검증 필수 (vibe 함정 #6).
 */
@Profile("local")
@Component
public class StubOAuthProvider implements OAuthProvider {

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode, String redirectUri) {
        throw new ApiException(
                org.springframework.http.HttpStatus.NOT_IMPLEMENTED,
                "OAUTH_NOT_CONFIGURED",
                "소셜 로그인이 아직 설정되지 않았습니다. 도급인 콘솔 키 수령 후 활성화됩니다."
        );
    }
}
