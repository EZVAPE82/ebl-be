package com.elfbarlounge.domain.qna.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.qna.application.ProductQnaService;
import com.elfbarlounge.domain.qna.domain.ProductQna;
import com.elfbarlounge.domain.qna.domain.ProductQnaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@Tag(name = "ProductQna")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductQnaController {

    private final ProductQnaService qnaService;
    private final ProductQnaRepository qnaRepository;

    /* ============================================================
     * 공개 조회 (비로그인 OK). isPrivate=true 인 문의는 question 마스킹.
     * ============================================================ */
    @Operation(summary = "상품별 Q&A 목록 (공개)")
    @GetMapping("/public/products/{productId}/qnas")
    public Page<QnaView> productQnas(
            @PathVariable Long productId,
            Pageable pageable,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Long viewerId = principal != null && principal.isUser() ? principal.memberId() : null;
        return qnaRepository
                .findByProductIdAndVisibleTrueOrderByCreatedAtDesc(productId, pageable)
                .map(q -> QnaView.from(q, viewerId));
    }

    /* ============================================================
     * 작성 (회원 인증 필요)
     * ============================================================ */
    @Operation(summary = "상품에 Q&A 작성")
    @PostMapping("/products/{productId}/qnas")
    public ResponseEntity<QnaView> ask(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long productId,
            @Valid @RequestBody AskRequest req
    ) {
        Long memberId = requireMember(principal);
        ProductQna q = qnaService.ask(productId, memberId, req.question(), Boolean.TRUE.equals(req.isPrivate()));
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + productId + "/qnas/" + q.getId()))
                .body(QnaView.from(q, memberId));
    }

    /* ============================================================
     * 내 Q&A 목록 (마이페이지)
     * ============================================================ */
    @Operation(summary = "내 Q&A 목록")
    @GetMapping("/members/me/qnas")
    public Page<QnaView> myQnas(
            @AuthenticationPrincipal AuthPrincipal principal,
            Pageable pageable
    ) {
        Long memberId = requireMember(principal);
        return qnaRepository
                .findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(q -> QnaView.from(q, memberId));
    }

    private Long requireMember(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }

    /* ============================================================
     * DTOs
     * ============================================================ */
    public record AskRequest(
            @NotBlank @Size(max = 2000) String question,
            Boolean isPrivate
    ) {}

    public record QnaView(
            Long id,
            Long productId,
            Long memberId,
            String question,         // private 이면서 viewer 가 작성자 아닐 때 마스킹
            String answer,
            Long answeredBy,
            LocalDateTime answeredAt,
            boolean isPrivate,
            LocalDateTime createdAt
    ) {
        public static QnaView from(ProductQna q, Long viewerId) {
            boolean canRead = !q.isPrivate() || (viewerId != null && viewerId.equals(q.getMemberId()));
            return new QnaView(
                    q.getId(),
                    q.getProductId(),
                    q.getMemberId(),
                    canRead ? q.getQuestion() : "비공개 문의입니다.",
                    canRead ? q.getAnswer() : null,
                    q.getAnsweredBy(),
                    q.getAnsweredAt(),
                    q.isPrivate(),
                    q.getCreatedAt()
            );
        }
    }
}
