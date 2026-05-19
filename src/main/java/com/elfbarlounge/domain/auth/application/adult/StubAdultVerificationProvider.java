package com.elfbarlounge.domain.auth.application.adult;

import com.elfbarlounge.common.exception.ApiException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * PASS CP 미계약 상태에서의 임시 구현.
 * 실제 호출 시 501. KMC/NICE 등 선정·계약 후 별도 구현체로 교체.
 */
@Profile("local")
@Component
public class StubAdultVerificationProvider implements AdultVerificationProvider {

    @Override
    public VerificationResult verify(String authToken) {
        throw new ApiException(
                HttpStatus.NOT_IMPLEMENTED,
                "PASS_NOT_CONFIGURED",
                "성인 인증이 아직 설정되지 않았습니다. 도급인 CP 계약 후 활성화됩니다."
        );
    }
}
