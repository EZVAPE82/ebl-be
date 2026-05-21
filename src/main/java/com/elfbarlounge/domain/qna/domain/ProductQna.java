package com.elfbarlounge.domain.qna.domain;

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

import java.time.LocalDateTime;

/**
 * 상품 Q&A — 회원이 상품에 대해 문의 작성, 어드민이 답변.
 * isPrivate=true 인 경우 작성자 본인 + 어드민만 조회 (서비스 레이어 필터).
 */
@Getter
@Entity
@Table(name = "product_qnas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductQna extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "question", length = 2000, nullable = false)
    private String question;

    @Column(name = "answer", length = 2000)
    private String answer;

    @Column(name = "answered_by")
    private Long answeredBy;          // admin_users.id

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Builder
    private ProductQna(Long productId, Long memberId, String question, Boolean isPrivate) {
        this.productId = productId;
        this.memberId = memberId;
        this.question = question;
        this.isPrivate = isPrivate != null && isPrivate;
        this.visible = true;
    }

    public void answer(Long adminId, String answer) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("answer required");
        }
        this.answer = answer;
        this.answeredBy = adminId;
        this.answeredAt = LocalDateTime.now();
    }

    public void hide() {
        this.visible = false;
    }
}
