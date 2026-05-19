package com.elfbarlounge.domain.content.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "banner_url", columnDefinition = "TEXT")
    private String bannerUrl;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Builder
    private Event(String title, String summary, String content, String bannerUrl,
                  LocalDateTime startsAt, LocalDateTime endsAt, Boolean visible) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.bannerUrl = bannerUrl;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.visible = visible == null || visible;
    }

    public void update(String title, String summary, String content, String bannerUrl,
                       LocalDateTime startsAt, LocalDateTime endsAt, boolean visible) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.bannerUrl = bannerUrl;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.visible = visible;
    }

    public boolean isOngoing() {
        if (!visible) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startsAt != null && now.isBefore(startsAt)) return false;
        if (endsAt != null && now.isAfter(endsAt)) return false;
        return true;
    }
}
