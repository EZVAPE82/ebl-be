package com.elfbarlounge.domain.marketplace.application;

import com.elfbarlounge.common.config.IntegrationProperties;
import com.elfbarlounge.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 네이버 스마트스토어 커머스 API 클라이언트 (stub).
 *
 * 활성화: app.integrations.naver-commerce.enabled=true + 클라이언트 ID/Secret 주입.
 * 실 호출 코드는 도급인 키 수령 후 채울 것.
 *
 * 참고:
 *  - OAuth2 client_credentials grant + 서명 토큰
 *  - https://apicenter.commerce.naver.com/ko/basic/commerce-api
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NaverCommerceClient implements MarketplaceClient {

    private final IntegrationProperties integrations;

    @Override
    public Marketplace getMarketplace() {
        return Marketplace.NAVER_SMARTSTORE;
    }

    @Override
    public boolean isEnabled() {
        return integrations.naverCommerce().enabled();
    }

    @Override
    public void pushProduct(MarketplaceProduct product) {
        checkEnabled();
        // TODO: POST https://api.commerce.naver.com/external/v2/products
        log.info("[NaverCommerce] pushProduct id={} (stub)", product.productId());
    }

    @Override
    public List<MarketplaceOrder> fetchNewOrders() {
        checkEnabled();
        // TODO: GET /external/v1/pay-order/seller/product-orders
        log.info("[NaverCommerce] fetchNewOrders (stub)");
        return List.of();
    }

    @Override
    public void pushTrackingNumber(String marketplaceOrderId, String courier, String trackingNo) {
        checkEnabled();
        // TODO: POST /external/v1/pay-order/seller/product-orders/{id}/dispatch
        log.info("[NaverCommerce] pushTracking orderId={} (stub)", marketplaceOrderId);
    }

    private void checkEnabled() {
        if (!isEnabled()) {
            throw new ApiException(
                    HttpStatus.NOT_IMPLEMENTED,
                    "NAVER_COMMERCE_DISABLED",
                    "네이버 커머스 연동이 비활성 상태입니다. (무니코틴 전환 시 활성화)"
            );
        }
    }
}
