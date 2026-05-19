package com.elfbarlounge.domain.member.api.dto;

import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberStatus;
import com.elfbarlounge.domain.member.domain.MemberType;

import java.time.LocalDate;

/**
 * 회원 응답 DTO. 민감정보(passwordHash, ci 등)는 절대 노출 X (vibe 함정 #4).
 * 이름·휴대폰은 마스킹 처리.
 */
public record MemberResponse(
        Long id,
        String email,
        String name,           // 마스킹
        String phone,          // 마스킹
        LocalDate birthDate,
        String gender,
        MemberType memberType,
        MemberStatus status
) {
    public static MemberResponse from(Member m) {
        return new MemberResponse(
                m.getId(),
                m.getEmail(),
                maskName(m.getName()),
                maskPhone(m.getPhone()),
                m.getBirthDate(),
                m.getGender(),
                m.getMemberType(),
                m.getStatus()
        );
    }

    private static String maskName(String name) {
        if (name == null || name.isBlank()) return null;
        if (name.length() == 1) return "*";
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    private static String maskPhone(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() < 8) return "***";
        return digits.substring(0, 3) + "-****-" + digits.substring(digits.length() - 4);
    }
}
