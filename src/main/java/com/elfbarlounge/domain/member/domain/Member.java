package com.elfbarlounge.domain.member.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 엔티티.
 *
 * 보안 메모:
 * - password_hash: BCrypt만 (rule 2). 평문/MD5/SHA1 절대 X.
 * - name·phone: 추후 컬럼 암호화 적용 검토 (Hibernate ColumnTransformer 또는 AttributeConverter).
 * - email은 UNIQUE이며 자체 가입 시 필수, 소셜 전용 가입 시 nullable.
 * - WITHDRAWN 시 개인정보 컬럼은 즉시 익명화/NULL, 결제·주문 데이터는 익명화하여 보관.
 */
@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "name", length = 80)
    private String name;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 8)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", length = 20, nullable = false)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_channel", length = 16)
    private JoinChannel joinChannel;

    @Column(name = "referrer_member_id")
    private Long referrerMemberId;

    @Column(name = "marketing_email_agreed", nullable = false)
    private boolean marketingEmailAgreed;

    @Column(name = "marketing_sms_agreed", nullable = false)
    private boolean marketingSmsAgreed;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private Member(String email, String passwordHash, String name, String phone,
                   LocalDate birthDate, String gender, MemberType memberType,
                   JoinChannel joinChannel, Long referrerMemberId,
                   boolean marketingEmailAgreed, boolean marketingSmsAgreed) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.memberType = memberType != null ? memberType : MemberType.KOREAN;
        this.status = MemberStatus.PENDING; // 가입 시 PENDING, 성인인증 완료 후 ACTIVE
        this.joinChannel = joinChannel;
        this.referrerMemberId = referrerMemberId;
        this.marketingEmailAgreed = marketingEmailAgreed;
        this.marketingSmsAgreed = marketingSmsAgreed;
    }

    /** 성인인증 완료 시 활성화 */
    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    /** 로그인 성공 시 호출 */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /** 1년 미접속 휴면 전환 */
    public void toDormant() {
        this.status = MemberStatus.DORMANT;
    }

    /**
     * 탈퇴 처리: 개인정보 익명화.
     * 결제·주문 데이터는 전자상거래법상 5년 보관 (다른 도메인에서 익명화 처리).
     */
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.email = null;
        this.passwordHash = null;
        this.name = null;
        this.phone = null;
        this.birthDate = null;
        this.gender = null;
        this.marketingEmailAgreed = false;
        this.marketingSmsAgreed = false;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    public void updateMarketingAgreement(boolean email, boolean sms) {
        this.marketingEmailAgreed = email;
        this.marketingSmsAgreed = sms;
    }
}
