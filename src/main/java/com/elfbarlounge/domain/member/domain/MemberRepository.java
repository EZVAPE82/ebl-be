package com.elfbarlounge.domain.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    /** phone으로 회원 조회 — 아이디 찾기에서 사용. 운영 인덱스: idx_members_phone */
    Optional<Member> findByPhoneAndStatusNot(String phone, MemberStatus excludeStatus);
}
