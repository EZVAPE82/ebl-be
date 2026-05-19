package com.elfbarlounge.domain.member.domain;

/**
 * 성인인증 처리 분기용 회원 유형.
 * @see docs/security-guidelines.md 외국인 케이스별 처리
 */
public enum MemberType {
    /** 내국인 (PASS 자동 인증) */
    KOREAN,
    /** 국내거주 외국인 (외국인등록번호 기반 PASS) */
    FOREIGN_RESIDENT,
    /** 해외거주 외국인 (여권 업로드 + 어드민 수동 승인) */
    FOREIGN_OVERSEAS
}
