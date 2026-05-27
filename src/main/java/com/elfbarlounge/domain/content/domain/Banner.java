package com.elfbarlounge.domain.content.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
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
@Table(name = "banners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement", length = 20, nullable = false)
    private Placement placement;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    @Column(name = "alt_text", length = 200)
    private String altText;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Builder
    private Banner(Placement placement, String imageUrl, String linkUrl, String altText,
                   Integer sortOrder, Boolean visible, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.placement = placement;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.altText = altText;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.visible = visible == null || visible;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public void update(Placement placement, String imageUrl, String linkUrl, String altText,
                       int sortOrder, boolean visible, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.placement = placement;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.altText = altText;
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

    public enum Placement { MAIN_HERO, MID_HERO, TOP_STRIP, SECTION }
}
