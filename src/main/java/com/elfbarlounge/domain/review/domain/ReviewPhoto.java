package com.elfbarlounge.domain.review.domain;

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
@Table(name = "review_photos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "url", columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    private ReviewPhoto(String url, int sortOrder) {
        this.url = url;
        this.sortOrder = sortOrder;
    }
}
