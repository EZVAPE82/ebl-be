package com.elfbarlounge.domain.marketplace.application;

import com.elfbarlounge.common.config.IntegrationProperties;
import com.elfbarlounge.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 11번가 OpenAPI 클라이언트 (stub).
 *
 * 활성화: app.integrations.eleven-openapi.enabled=true + API Key 주입.
 *
 * 참고:
 *  - https://openapi.11st.co.kr/
 *  - XML/JSON 두 방식 모두 지원, 응답은 마켓별 매핑 필요
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElevenOpenApiClient implements MarketplaceClient {

    private final IntegrationProperties integrations;

    @Override
    public Marketplace getMarketplace() {
        return Marketplace.ELEVEN;
    }

    @Override
    public boolean isEnabled() {
        return integrations.elevenOpenapi().enabled();
    }

    @Override
    public void pushProduct(MarketplaceProduct product) {
        checkEnabled();
        log.info("[ElevenOpenAPI] pushProduct id={} (stub)", product.productId());
    }

    @Override
    public List<MarketplaceOrder> fetchNewOrders() {
        checkEnabled();
        log.info("[ElevenOpenAPI] fetchNewOrders (stub)");
        return List.of();
    }

    @Override
    public void pushTrackingNumber(String marketplaceOrderId, String courier, String trackingNo) {
        checkEnabled();
        log.info("[ElevenOpenAPI] pushTracking orderId={} (stub)", marketplaceOrderId);
    }

    private void checkEnabled() {
        if (!isEnabled()) {
            throw new ApiException(
                    HttpStatus.NOT_IMPLEMENTED,
                    "ELEVEN_OPENAPI_DISABLED",
                    "11번가 OpenAPI 연동이 비활성 상태입니다. (무니코틴 전환 시 활성화)"
            );
        }
    }
}
