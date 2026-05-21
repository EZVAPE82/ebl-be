package com.elfbarlounge.domain.qna.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.qna.domain.ProductQna;
import com.elfbarlounge.domain.qna.domain.ProductQnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductQnaService {

    private final ProductQnaRepository qnaRepository;

    @Transactional
    public ProductQna ask(Long productId, Long memberId, String question, boolean isPrivate) {
        if (question == null || question.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "QNA_INVALID", "문의 내용을 입력해주세요.");
        }
        if (question.length() > 2000) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "QNA_TOO_LONG", "문의는 2000자 이내로 작성해주세요.");
        }
        ProductQna q = ProductQna.builder()
                .productId(productId)
                .memberId(memberId)
                .question(question.trim())
                .isPrivate(isPrivate)
                .build();
        return qnaRepository.save(q);
    }

    @Transactional
    public ProductQna answer(Long qnaId, Long adminId, String answer) {
        ProductQna q = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "QNA_NOT_FOUND", "문의를 찾을 수 없습니다."));
        q.answer(adminId, answer);
        return q;
    }

    @Transactional
    public void hide(Long qnaId) {
        ProductQna q = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "QNA_NOT_FOUND", "문의를 찾을 수 없습니다."));
        q.hide();
    }
}
