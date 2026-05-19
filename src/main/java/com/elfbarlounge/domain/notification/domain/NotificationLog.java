package com.elfbarlounge.domain.notification.domain;

import com.elfbarlounge.domain.notification.application.NotificationSender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notification_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 16, nullable = false)
    private NotificationSender.Channel channel;

    @Column(name = "template_code", length = 40, nullable = false)
    private String templateCode;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private NotificationSender.Status status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private NotificationLog(Long memberId, NotificationSender.Channel channel, String templateCode,
                            String payload, NotificationSender.Status status,
                            String failureReason, LocalDateTime sentAt) {
        this.memberId = memberId;
        this.channel = channel;
        this.templateCode = templateCode;
        this.payload = payload;
        this.status = status;
        this.failureReason = failureReason;
        this.sentAt = sentAt != null ? sentAt : LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}
