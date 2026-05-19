package com.elfbarlounge.domain.auth.api.dto;

import com.elfbarlounge.domain.member.domain.JoinChannel;
import com.elfbarlounge.domain.member.domain.MemberType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 회원가입 요청 DTO.
 * 모든 사용자 입력에 검증 적용 (rule 4).
 */
public record SignupRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        @Size(max = 255)
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 10, max = 100, message = "비밀번호는 10자 이상 100자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{10,}$",
                message = "비밀번호는 영문·숫자·특수문자를 각 1자 이상 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 80)
        String name,

        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        @Pattern(regexp = "^[0-9+\\-]{8,20}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        String phone,

        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birthDate,

        @Size(max = 8)
        String gender,

        MemberType memberType,

        JoinChannel joinChannel,

        String referralCode,

        boolean marketingEmailAgreed,
        boolean marketingSmsAgreed,

        @AssertTrue(message = "이용약관 동의는 필수입니다.")
        boolean tosAgreed,

        @AssertTrue(message = "개인정보 처리방침 동의는 필수입니다.")
        boolean privacyAgreed,

        @AssertTrue(message = "청소년보호정책 동의는 필수입니다.")
        boolean youthAgreed
) {
}
