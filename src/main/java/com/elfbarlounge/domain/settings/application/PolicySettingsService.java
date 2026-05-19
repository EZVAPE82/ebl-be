package com.elfbarlounge.domain.settings.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.settings.domain.PolicySetting;
import com.elfbarlounge.domain.settings.domain.PolicySettingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 정책 설정값 서비스.
 *
 * 캐시 전략:
 *  - 부팅 시 전체 로드 → 인메모리 Map
 *  - update 시 캐시 갱신
 *  - 단일 인스턴스 가정 (분산 시 Redis pub/sub로 무효화 필요)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PolicySettingsService {

    private final PolicySettingRepository repository;
    private final Map<String, String> cache = new HashMap<>();

    // 키 상수 (오타 방지)
    public static final String FREE_SHIPPING_THRESHOLD = "free_shipping_threshold";
    public static final String POINT_MIN_USE_AMOUNT = "point_min_use_amount";
    public static final String POINT_MIN_BALANCE = "point_min_balance";
    public static final String POINT_MAX_USE_RATE = "point_max_use_rate";
    public static final String RETURN_SHIPPING_FEE = "return_shipping_fee";
    public static final String COUPON_POINT_CONCURRENT = "coupon_point_concurrent";
    public static final String SHIPPING_FEE_DEFAULT = "shipping_fee_default";

    @PostConstruct
    public void preload() {
        cache.clear();
        repository.findAll().forEach(s -> cache.put(s.getKey(), s.getValue()));
        log.info("PolicySettings preloaded: {} entries", cache.size());
    }

    public String get(String key) {
        return cache.get(key);
    }

    public long getLong(String key, long defaultValue) {
        String v = cache.get(key);
        if (v == null) return defaultValue;
        try { return Long.parseLong(v); } catch (NumberFormatException e) { return defaultValue; }
    }

    public boolean getBool(String key, boolean defaultValue) {
        String v = cache.get(key);
        if (v == null) return defaultValue;
        return Boolean.parseBoolean(v);
    }

    @Transactional
    public void update(String key, String value) {
        PolicySetting s = repository.findById(key)
                .orElseThrow(() -> ApiException.notFound("SETTING_NOT_FOUND",
                        "설정 키를 찾을 수 없습니다: " + key));
        s.update(value);
        cache.put(key, value);
        log.info("PolicySetting updated: {}={}", key, value);
    }

    @Transactional(readOnly = true)
    public List<PolicySetting> all() {
        return repository.findAll();
    }

    /**
     * 무료배송 적용 후 배송비 계산.
     */
    public long calcShippingFee(long productAmount) {
        long threshold = getLong(FREE_SHIPPING_THRESHOLD, 50_000);
        if (productAmount >= threshold) return 0;
        return getLong(SHIPPING_FEE_DEFAULT, 3000);
    }
}
