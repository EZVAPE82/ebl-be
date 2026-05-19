package com.elfbarlounge.common.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 인메모리 토큰 버킷 기반 Rate Limiter.
 *
 * 분산 환경에선 Redis (Redisson RateLimiter) 권장. MVP는 단일 인스턴스 가정.
 *
 * 동작:
 *  - key (예: IP주소·이메일) 별로 버킷 보관
 *  - 시간 경과만큼 토큰 보충
 *  - tryConsume() 호출 시 1 토큰 차감, 0이면 false
 *
 * 보안 룰 #8 — 로그인·회원가입·결제·본인인증 등 민감 endpoint에 적용
 */
@Component
public class RateLimiter {

    private final ConcurrentHashMap<String, AtomicReference<Bucket>> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int capacity, Duration window) {
        AtomicReference<Bucket> ref = buckets.computeIfAbsent(key,
                k -> new AtomicReference<>(new Bucket(capacity, System.nanoTime())));

        long refillIntervalNanos = window.toNanos() / Math.max(1, capacity);

        while (true) {
            Bucket current = ref.get();
            long now = System.nanoTime();
            long elapsed = now - current.lastRefillNanos();
            int refill = (int) (elapsed / refillIntervalNanos);
            int newTokens = Math.min(capacity, current.tokens() + refill);
            long newRefillTime = refill > 0
                    ? current.lastRefillNanos() + (long) refill * refillIntervalNanos
                    : current.lastRefillNanos();

            if (newTokens <= 0) {
                // 시도조차 못함 — 버킷 갱신 후 false
                if (refill > 0) {
                    ref.compareAndSet(current, new Bucket(0, newRefillTime));
                }
                return false;
            }

            Bucket updated = new Bucket(newTokens - 1, newRefillTime);
            if (ref.compareAndSet(current, updated)) {
                return true;
            }
            // CAS 실패 → 재시도
        }
    }

    /** 테스트·디버깅용 — 키별 현재 토큰 수. */
    public int peek(String key) {
        AtomicReference<Bucket> ref = buckets.get(key);
        return ref == null ? -1 : ref.get().tokens();
    }

    /** 메모리 누수 방지 — 운영에서는 cron으로 호출하거나 ConcurrentHashMap.size() 모니터링. */
    public int size() {
        return buckets.size();
    }

    private record Bucket(int tokens, long lastRefillNanos) {}
}
