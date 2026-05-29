package com.elfbarlounge.domain.notification.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 알림 스텁. SENT 로그만 남기고 실제 발송하지 않음.
 *
 * 운영에서 실제 sender (KakaoAlimtalkSender, SesEmailSender 등) 가 등록되면
 * 이 클래스를 삭제하거나 @Profile("!prod") 로 비활성화. 지금은 prod 에서도
 * stub 으로 동작 (부팅 보장).
 *
 * 개인정보를 로그에 그대로 찍지 않도록 주의 (rule 9).
 */
@Slf4j
@Component
public class StubNotificationSender implements NotificationSender {

    @Override
    public Result send(Long memberId, Channel channel, String templateCode, Map<String, String> variables) {
        log.info("[StubNotification] memberId={} channel={} template={}", memberId, channel, templateCode);
        return Result.sent();
    }
}
