package com.elfbarlounge.domain.notification.application;

import java.util.Map;

/**
 * 알림 발송 인터페이스. 카카오 비즈메시지(알림톡) 또는 이메일 폴백.
 * 도급인 발신프로필 등록 후 실 구현체로 교체.
 */
public interface NotificationSender {

    Result send(Long memberId, Channel channel, String templateCode, Map<String, String> variables);

    enum Channel { ALIMTALK, EMAIL, SMS }
    enum Status { SENT, FAILED, SKIPPED }

    record Result(Status status, String failureReason) {
        public static Result sent() { return new Result(Status.SENT, null); }
        public static Result failed(String reason) { return new Result(Status.FAILED, reason); }
        public static Result skipped(String reason) { return new Result(Status.SKIPPED, reason); }
    }
}
