package com.elfbarlounge.domain.product.domain;

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
@Table(name = "brands")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 80, nullable = false, unique = true)
    private String name;

    @Column(name = "slug", length = 80, nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    private Brand(String name, String slug, String logoUrl, int sortOrder) {
        this.name = name;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.sortOrder = sortOrder;
    }

    public void update(String name, String slug, String logoUrl, int sortOrder) {
        this.name = name;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.sortOrder = sortOrder;
    }
}
