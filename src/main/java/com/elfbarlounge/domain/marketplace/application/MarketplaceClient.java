package com.elfbarlounge.domain.marketplace.application;

import java.util.List;

/**
 * 오픈마켓 직접 API 연동 인터페이스.
 *
 * 현재 비활성. 무니코틴 전환 시:
 *  - 도급인이 셀러 가입(스마트스토어 / 11번가)
 *  - 커머스 API 사용 신청 + Client ID/Secret 발급
 *  - 환경변수 NAVER_COMMERCE_ENABLED=true, ELEVEN_OPENAPI_ENABLED=true
 *  - Stub → 실 구현체(NaverCommerceClient, ElevenOpenApiClient)로 자동 교체
 *
 * 본 인터페이스는 마켓별 차이를 추상화. 운영 시 마켓별 별도 구현 OK.
 */
public interface MarketplaceClient {

    Marketplace getMarketplace();

    boolean isEnabled();

    /** 자사몰 상품을 마켓에 등록·갱신. */
    void pushProduct(MarketplaceProduct product);

    /** 마켓에서 신규 주문 수집. */
    List<MarketplaceOrder> fetchNewOrders();

    /** 마켓 주문에 송장 정보 push. */
    void pushTrackingNumber(String marketplaceOrderId, String courier, String trackingNo);

    enum Marketplace {
        NAVER_SMARTSTORE,
        ELEVEN
    }

    record MarketplaceProduct(
            Long productId, String name, long price,
            int stock, String description, String thumbnailUrl
    ) {}

    record MarketplaceOrder(
            String marketplaceOrderId,
            Marketplace marketplace,
            Long productId,
            int quantity,
            long paidAmount,
            String recipientName,
            String recipientPhone,
            String postalCode,
            String address1,
            String address2
    ) {}
}
