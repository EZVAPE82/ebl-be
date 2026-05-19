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
@Table(name = "popups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Popup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Builder
    private Popup(String title, String imageUrl, String linkUrl, String contentHtml,
                  Integer sortOrder, Boolean visible, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.contentHtml = contentHtml;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.visible = visible == null || visible;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public void update(String title, String imageUrl, String linkUrl, String contentHtml,
                       int sortOrder, boolean visible, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.contentHtml = contentHtml;
        this.sortOrder = sortOrder;
        this.visible = visible;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public boolean isCurrentlyVisible() {
        if (!visible) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startsAt != null && now.isBefore(startsAt)) return false;
        if (endsAt != null && now.isAfter(endsAt)) return false;
        return true;
    }
}
