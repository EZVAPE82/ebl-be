package com.elfbarlounge.domain.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    List<MemberAddress> findByMemberIdOrderByIsDefaultDescIdAsc(Long memberId);
    Optional<MemberAddress> findByIdAndMemberId(Long id, Long memberId);
    long countByMemberId(Long memberId);
}
