package com.elfbarlounge.domain.marketplace.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 오픈마켓 동기화 facade. 활성 마켓만 처리.
 *
 * 현재는 모든 클라이언트가 토글 비활성이라 실제 호출 없음 — 무니코틴 전환 후 자동 활성.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplaceSyncService {

    private final List<MarketplaceClient> clients;

    /** 활성 마켓 목록 (현재 비활성이면 빈 리스트) */
    public List<MarketplaceClient.Marketplace> activeMarkets() {
        return clients.stream()
                .filter(MarketplaceClient::isEnabled)
                .map(MarketplaceClient::getMarketplace)
                .toList();
    }

    public void pushProductToAll(MarketplaceClient.MarketplaceProduct product) {
        clients.stream()
                .filter(MarketplaceClient::isEnabled)
                .forEach(c -> {
                    try {
                        c.pushProduct(product);
                    } catch (Exception e) {
                        log.warn("Marketplace pushProduct failed: market={}, msg={}", c.getMarketplace(), e.getMessage());
                    }
                });
    }
}
