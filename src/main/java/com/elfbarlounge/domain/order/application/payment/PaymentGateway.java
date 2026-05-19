package com.elfbarlounge.domain.order.application.payment;

/**
 * PG 결제 게이트웨이 인터페이스.
 * 도급인이 포트원 V2 가입 후 PortonePaymentGateway 구현체로 교체.
 */
public interface PaymentGateway {

    /**
     * 결제 요청. 카드 정보 자체는 클라이언트가 PG로 직접 송신하고 우리는 결제 토큰만 받아 검증.
     * @param amount 결제 금액
     * @param orderNo 주문 번호 (멱등성·정합성 키)
     * @param token PG가 발급한 결제 토큰 (PG-V2 paymentKey 등)
     * @return 거래 ID·승인 결과
     */
    PaymentResult charge(String orderNo, long amount, String token, String method);

    void cancel(String pgTxId, long amount, String reason);

    record PaymentResult(boolean ok, String pgTxId, String rawResponse) {}
}
