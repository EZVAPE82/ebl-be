package com.elfbarlounge.domain.auth.application.adult;

import java.time.LocalDate;

/**
 * 성인인증 Provider.
 *
 * 케이스 (v1.5 2.7.1):
 *  1. 내국인 - PASS 자동
 *  2. 국내거주 외국인 - 외국인등록번호 기반 PASS
 *  3. 해외거주 외국인 - 여권 업로드 + 어드민 수동 승인 (이건 별도 흐름)
 *
 * 본 인터페이스는 1, 2 케이스(자동 PASS)를 다룬다. 3은 별도 admin 도메인.
 * 구현체는 도급인이 KMC/NICE CP 계약 후 채울 것 (PassKmcProvider 등).
 */
public interface AdultVerificationProvider {

    /**
     * 본인인증 결과 토큰을 검증하고 CI/이름/생년월일 반환.
     * 만 19세 미만이면 호출 측에서 거부 처리.
     */
    VerificationResult verify(String authToken);

    record VerificationResult(
            String ci,           // 본인 식별값 (DB 저장 시 암호화)
            String name,
            LocalDate birthDate,
            boolean foreign,
            String gender
    ) {
        public int ageAt(LocalDate now) {
            return now.getYear() - birthDate.getYear()
                    - (now.getDayOfYear() < birthDate.getDayOfYear() ? 1 : 0);
        }
    }
}
