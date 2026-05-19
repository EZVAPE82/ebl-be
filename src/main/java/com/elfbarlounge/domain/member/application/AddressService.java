package com.elfbarlounge.domain.member.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.member.domain.MemberAddress;
import com.elfbarlounge.domain.member.domain.MemberAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 회원 배송지 CRUD. 최대 5개 (기능명세서 v1.5 2.1.5.3).
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private static final int MAX_PER_MEMBER = 5;

    private final MemberAddressRepository repository;

    @Transactional(readOnly = true)
    public List<MemberAddress> list(Long memberId) {
        return repository.findByMemberIdOrderByIsDefaultDescIdAsc(memberId);
    }

    @Transactional
    public Long create(Long memberId, AddressInput in) {
        if (repository.countByMemberId(memberId) >= MAX_PER_MEMBER) {
            throw ApiException.badRequest("ADDRESS_LIMIT_EXCEEDED",
                    "배송지는 최대 " + MAX_PER_MEMBER + "개까지 등록 가능합니다.");
        }
        boolean isDefault = in.isDefault();
        if (isDefault) {
            unsetDefault(memberId);
        } else if (repository.countByMemberId(memberId) == 0) {
            // 첫 등록이면 자동 기본
            isDefault = true;
        }
        MemberAddress saved = repository.save(MemberAddress.builder()
                .memberId(memberId)
                .label(in.label())
                .recipientName(in.recipientName())
                .phone(in.phone())
                .postalCode(in.postalCode())
                .address1(in.address1())
                .address2(in.address2())
                .isDefault(isDefault)
                .build());
        return saved.getId();
    }

    @Transactional
    public void update(Long memberId, Long id, AddressInput in) {
        MemberAddress a = repository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> ApiException.notFound("ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다."));
        if (in.isDefault() && !a.isDefault()) {
            unsetDefault(memberId);
        }
        a.update(in.label(), in.recipientName(), in.phone(), in.postalCode(),
                in.address1(), in.address2(), in.isDefault());
    }

    @Transactional
    public void delete(Long memberId, Long id) {
        MemberAddress a = repository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> ApiException.notFound("ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다."));
        repository.delete(a);
    }

    @Transactional
    public void setDefault(Long memberId, Long id) {
        MemberAddress target = repository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> ApiException.notFound("ADDRESS_NOT_FOUND", "배송지를 찾을 수 없습니다."));
        unsetDefault(memberId);
        target.setAsDefault(true);
    }

    private void unsetDefault(Long memberId) {
        repository.findByMemberIdOrderByIsDefaultDescIdAsc(memberId)
                .forEach(a -> a.setAsDefault(false));
    }

    public record AddressInput(
            String label,
            String recipientName,
            String phone,
            String postalCode,
            String address1,
            String address2,
            boolean isDefault
    ) {}
}
