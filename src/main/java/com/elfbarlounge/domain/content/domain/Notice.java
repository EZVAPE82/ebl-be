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

@Getter
@Entity
@Table(name = "notices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Builder
    private Notice(String title, String content, Boolean pinned, Boolean visible) {
        this.title = title;
        this.content = content;
        this.pinned = pinned != null && pinned;
        this.visible = visible == null || visible;
    }

    public void update(String title, String content, boolean pinned, boolean visible) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.visible = visible;
    }

    public void increaseView() {
        this.viewCount++;
    }
}
