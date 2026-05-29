package com.elfbarlounge.domain.notification.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 알림 스텁. SENT 로그만 남기고 실제 발송하지 않음.
 *
 * 운영에서 카카오 비즈메시지(KakaoAlimtalkSender), SesEmailSender 등이 별도 빈으로
 * 등록되면 @ConditionalOnMissingBean 으로 자동 비활성. 그때까지는 prod 에서도
 * stub 으로 동작 (부팅 보장).
 *
 * 개인정보를 로그에 그대로 찍지 않도록 주의 (rule 9).
 */
@Slf4j
@Component
@ConditionalOnMissingBean(NotificationSender.class)
public class StubNotificationSender implements NotificationSender {

    @Override
    public Result send(Long memberId, Channel channel, String templateCode, Map<String, String> variables) {
        log.info("[StubNotification] memberId={} channel={} template={}", memberId, channel, templateCode);
        return Result.sent();
    }
}
