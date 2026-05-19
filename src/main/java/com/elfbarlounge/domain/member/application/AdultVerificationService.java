package com.elfbarlounge.domain.member.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.member.domain.AdultVerification;
import com.elfbarlounge.domain.member.domain.AdultVerificationRepository;
import com.elfbarlounge.domain.member.domain.Member;
import com.elfbarlounge.domain.member.domain.MemberRepository;
import com.elfbarlounge.domain.member.domain.MemberType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdultVerificationService {

    private final AdultVerificationRepository verificationRepository;
    private final MemberRepository memberRepository;

    /** 해외거주 외국인 신청. PENDING 상태로 어드민 검토 대기. */
    @Transactional
    public Long submitForeignVerification(Long memberId, String s3Key) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));

        if (member.getMemberType() != MemberType.FOREIGN_OVERSEAS) {
            throw ApiException.badRequest("MEMBER_TYPE_MISMATCH",
                    "해외거주 외국인 회원만 신청할 수 있습니다.");
        }

        // 이미 PENDING 신청이 있으면 중복 방지
        verificationRepository.findFirstByMemberIdAndStatusOrderByCreatedAtDesc(memberId, AdultVerification.Status.PENDING)
                .ifPresent(v -> {
                    throw ApiException.conflict("VERIFICATION_PENDING",
                            "이미 검토 대기 중인 신청이 있습니다.");
                });

        AdultVerification v = AdultVerification.builder()
                .memberId(memberId)
                .method(AdultVerification.Method.FOREIGN_DOC)
                .status(AdultVerification.Status.PENDING)
                .documentUrl(s3Key)
                .build();
        return verificationRepository.save(v).getId();
    }

    @Transactional
    public void approve(Long verificationId, Long adminUserId) {
        AdultVerification v = verificationRepository.findById(verificationId)
                .orElseThrow(() -> ApiException.notFound("VERIFICATION_NOT_FOUND", "신청을 찾을 수 없습니다."));
        if (v.getStatus() != AdultVerification.Status.PENDING) {
            throw ApiException.conflict("VERIFICATION_ALREADY_REVIEWED", "이미 처리된 신청입니다.");
        }
        v.approve(adminUserId);

        // 회원 활성화
        Member member = memberRepository.findById(v.getMemberId())
                .orElseThrow(() -> ApiException.notFound("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        member.activate();
        log.info("Adult verification approved: verificationId={}, memberId={}, adminUserId={}",
                verificationId, v.getMemberId(), adminUserId);
    }

    @Transactional
    public void reject(Long verificationId, Long adminUserId, String reason) {
        AdultVerification v = verificationRepository.findById(verificationId)
                .orElseThrow(() -> ApiException.notFound("VERIFICATION_NOT_FOUND", "신청을 찾을 수 없습니다."));
        if (v.getStatus() != AdultVerification.Status.PENDING) {
            throw ApiException.conflict("VERIFICATION_ALREADY_REVIEWED", "이미 처리된 신청입니다.");
        }
        v.reject(adminUserId, reason);
        log.info("Adult verification rejected: verificationId={}, memberId={}, adminUserId={}, reason={}",
                verificationId, v.getMemberId(), adminUserId, reason);
    }

    @Transactional(readOnly = true)
    public Page<AdultVerification> listPending(Pageable pageable) {
        return verificationRepository.findByStatusOrderByCreatedAtAsc(AdultVerification.Status.PENDING, pageable);
    }
}
