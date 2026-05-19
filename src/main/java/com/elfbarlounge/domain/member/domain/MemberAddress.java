package com.elfbarlounge.domain.member.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "label", length = 40)
    private String label;

    @Column(name = "recipient_name", length = 80, nullable = false)
    private String recipientName;

    @Column(name = "phone", length = 32, nullable = false)
    private String phone;

    @Column(name = "postal_code", length = 16, nullable = false)
    private String postalCode;

    @Column(name = "address1", length = 200, nullable = false)
    private String address1;

    @Column(name = "address2", length = 200)
    private String address2;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    private MemberAddress(Long memberId, String label, String recipientName, String phone,
                          String postalCode, String address1, String address2, boolean isDefault) {
        this.memberId = memberId;
        this.label = label;
        this.recipientName = recipientName;
        this.phone = phone;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.isDefault = isDefault;
    }

    public void update(String label, String recipientName, String phone, String postalCode,
                       String address1, String address2, boolean isDefault) {
        this.label = label;
        this.recipientName = recipientName;
        this.phone = phone;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.isDefault = isDefault;
    }

    public void setAsDefault(boolean v) {
        this.isDefault = v;
    }
}
