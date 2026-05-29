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

/**
 * 후기 사진.
 *
 * 업로드 흐름:
 *  1. 사용자 사진 업로드 (다양한 비율 / 사이즈)
 *  2. 백엔드 ImageService 가 원본 저장 + thumbnail 2 종 자동 생성:
 *     - thumbnailUrl: 800x800 center crop (메인 그리드 + 리뷰 카드용)
 *     - thumbnailSm:  300x300 center crop (모바일 리스트용)
 *  3. width/height: 원본 사이즈 (lightbox 비율 계산용)
 *
 * 프론트:
 *  - 메인 그리드/리뷰 카드 → thumbnailUrl (800x800 정사각형, object-fit cover)
 *  - lightbox/상세 뷰 → url (원본 풀 사이즈)
 */
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

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "thumbnail_sm", columnDefinition = "TEXT")
    private String thumbnailSm;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "content_type", length = 40)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    private ReviewPhoto(String url, String thumbnailUrl, String thumbnailSm,
                        Integer width, Integer height, String contentType, Long fileSize, int sortOrder) {
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailSm = thumbnailSm;
        this.width = width;
        this.height = height;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.sortOrder = sortOrder;
    }
}
