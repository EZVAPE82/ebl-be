package com.elfbarlounge.domain.review.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "order_item_id", nullable = false, unique = true)
    private Long orderItemId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "has_photo", nullable = false)
    private boolean hasPhoto;

    @Column(name = "point_rewarded", nullable = false)
    private boolean pointRewarded;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "review_id")
    private List<ReviewPhoto> photos = new ArrayList<>();

    @Builder
    private Review(Long productId, Long memberId, Long orderItemId, int rating, String content) {
        this.productId = productId;
        this.memberId = memberId;
        this.orderItemId = orderItemId;
        this.rating = rating;
        this.content = content;
    }

    public void addPhoto(String url, int sortOrder) {
        this.photos.add(ReviewPhoto.builder().url(url).sortOrder(sortOrder).build());
        this.hasPhoto = true;
    }

    public void markPointRewarded() {
        this.pointRewarded = true;
    }
}
