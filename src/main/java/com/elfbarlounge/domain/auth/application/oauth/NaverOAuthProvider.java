package com.elfbarlounge.domain.auth.application.oauth;

import com.elfbarlounge.common.config.IntegrationProperties;
import com.elfbarlounge.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 네이버 로그인 Provider (현재 비활성 stub).
 *
 * 활성화 절차:
 *  1. 네이버 디벨로퍼스(developers.naver.com)에서 애플리케이션 등록
 *     — 무니코틴 카테고리 사이트로 신고 (담배·전자담배 카테고리는 검수 불가)
 *  2. Client ID/Secret 발급
 *  3. 환경변수 설정:
 *       NAVER_LOGIN_ENABLED=true
 *       NAVER_LOGIN_CLIENT_ID=...
 *       NAVER_LOGIN_CLIENT_SECRET=...
 *  4. 재배포 — 본 클래스가 enabled=true를 인식하면 실 호출 수행 코드 활성
 *
 * 토글 OFF 상태에서 호출 시 501 NOT_IMPLEMENTED.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOAuthProvider implements OAuthProvider {

    private final IntegrationProperties integrations;

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode, String redirectUri) {
        if (!integrations.naverLogin().enabled()) {
            throw new ApiException(
                    HttpStatus.NOT_IMPLEMENTED,
                    "NAVER_LOGIN_DISABLED",
                    "네이버 로그인은 현재 비활성 상태입니다. (무니코틴 전환 시 활성화 예정)"
            );
        }

        // TODO (활성화 시): 네이버 OAuth2 토큰 교환 + 프로필 조회
        // 1. POST https://nid.naver.com/oauth2.0/token
        //    grant_type=authorization_code & client_id & client_secret & code & state
        // 2. GET https://openapi.naver.com/v1/nid/me  (Authorization: Bearer access_token)
        // 3. response.response.id / email / name 매핑
        log.info("[NaverOAuthProvider] 실 연동 코드는 도급인 키 수령 후 활성화");
        throw new ApiException(
                HttpStatus.NOT_IMPLEMENTED,
                "NAVER_LOGIN_PENDING",
                "네이버 로그인 실 연동 코드가 아직 구현되지 않았습니다."
        );
    }
}
