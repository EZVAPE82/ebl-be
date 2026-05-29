package com.elfbarlounge.domain.order.application.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 결제 스텁. 무조건 성공으로 처리.
 * 운영 가기 전 PortonePaymentGateway 등 실제 구현체로 교체 (이 클래스를 삭제하거나
 * @Profile("!prod") 처리). 현재는 prod 부팅 보장 위해 모든 프로파일에서 동작.
 */
@Slf4j
@Component
public class StubPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(String orderNo, long amount, String token, String method) {
        String pgTxId = "stub-" + UUID.randomUUID();
        log.info("[StubPaymentGateway] charge ok orderNo={} amount={} method={} pgTxId={}",
                orderNo, amount, method, pgTxId);
        // 카드번호 등 민감정보를 절대 로그/응답에 남기지 않음 (rule 1, vibe 함정 #3)
        return new PaymentResult(true, pgTxId, "{\"stub\":true}");
    }

    @Override
    public void cancel(String pgTxId, long amount, String reason) {
        log.info("[StubPaymentGateway] cancel pgTxId={} amount={} reason={}", pgTxId, amount, reason);
    }
}
