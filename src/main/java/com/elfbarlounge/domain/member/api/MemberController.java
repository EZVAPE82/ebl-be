package com.elfbarlounge.domain.member.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.member.api.dto.MemberResponse;
import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @Operation(summary = "내 정보 조회 (JWT 인증 필요)")
    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
        if (principal == null) {
            throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        }
        Member m = memberRepository.findById(principal.memberId())
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        return MemberResponse.from(m);
    }
}
