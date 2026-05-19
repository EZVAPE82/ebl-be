package com.elfbarlounge.domain.notification.application;

import com.elfbarlounge.domain.notification.domain.NotificationLog;
import com.elfbarlounge.domain.notification.domain.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 발송 + 로그 저장 통합. 알림은 주문 트랜잭션에 영향 없도록 별도 트랜잭션(REQUIRES_NEW).
 *
 * 템플릿 코드 (알림톡):
 *  - ORDER_PAID         : 주문 완료
 *  - ORDER_SHIPPED      : 배송 시작
 *  - ORDER_DELIVERED    : 배송 완료
 *  - EMAIL_FALLBACK_*   : 이메일 폴백 (알림톡 실패 시)
 *
 * 보안:
 *  - 페이로드에 개인정보 평문 포함 X. 최소 식별자(orderNo, 마스킹된 수령인 등).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationSender sender;
    private final NotificationLogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long memberId, NotificationSender.Channel channel, String templateCode,
                     Map<String, String> variables) {
        NotificationSender.Result result;
        try {
            result = sender.send(memberId, channel, templateCode, variables);
        } catch (Exception e) {
            log.warn("Notification send threw: code={}, msg={}", templateCode, e.getMessage());
            result = NotificationSender.Result.failed("EXCEPTION:" + e.getClass().getSimpleName());
        }

        logRepository.save(NotificationLog.builder()
                .memberId(memberId)
                .channel(channel)
                .templateCode(templateCode)
                .payload(safePayload(variables))
                .status(result.status())
                .failureReason(result.failureReason())
                .sentAt(result.status() == NotificationSender.Status.SENT ? LocalDateTime.now() : null)
                .build());

        // 알림톡 실패 시 이메일 폴백 (단순)
        if (channel == NotificationSender.Channel.ALIMTALK
                && result.status() == NotificationSender.Status.FAILED) {
            sendFallback(memberId, templateCode, variables);
        }
    }

    private void sendFallback(Long memberId, String templateCode, Map<String, String> variables) {
        NotificationSender.Result result;
        try {
            result = sender.send(memberId, NotificationSender.Channel.EMAIL, templateCode, variables);
        } catch (Exception e) {
            result = NotificationSender.Result.failed("EXCEPTION:" + e.getClass().getSimpleName());
        }
        logRepository.save(NotificationLog.builder()
                .memberId(memberId)
                .channel(NotificationSender.Channel.EMAIL)
                .templateCode(templateCode)
                .payload(safePayload(variables))
                .status(result.status())
                .failureReason(result.failureReason())
                .sentAt(result.status() == NotificationSender.Status.SENT ? LocalDateTime.now() : null)
                .build());
    }

    /**
     * 페이로드 직렬화 — 개인정보는 키만 기록하고 값은 보존하지 않는 식으로 마스킹.
     * MVP는 키만 저장 (값 제외).
     */
    private String safePayload(Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) return "{}";
        return "{keys=" + String.join(",", variables.keySet()) + "}";
    }
}
