package com.elfbarlounge.domain.notification.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 로컬 개발용 알림 스텁. SENT 로그만 남기고 실제 발송하지 않음.
 * 운영에서는 카카오 비즈메시지(KakaoAlimtalkSender), SesEmailSender 등으로 교체.
 * 개인정보를 로그에 그대로 찍지 않도록 주의 (rule 9).
 */
@Slf4j
@Profile("local")
@Component
public class StubNotificationSender implements NotificationSender {

    @Override
    public Result send(Long memberId, Channel channel, String templateCode, Map<String, String> variables) {
        log.info("[StubNotification] memberId={} channel={} template={}", memberId, channel, templateCode);
        return Result.sent();
    }
}
