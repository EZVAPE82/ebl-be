package com.elfbarlounge.domain.member.domain;

public enum MemberStatus {
    /** 가입 직후, 성인인증 미완료 */
    PENDING,
    /** 정상 활성 */
    ACTIVE,
    /** 1년 미접속 휴면 (정보통신망법) */
    DORMANT,
    /** 탈퇴 */
    WITHDRAWN
}
