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
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Column(name = "slug", length = 80, nullable = false, unique = true)
    private String slug;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Builder
    private Category(Long parentId, String name, String slug, int sortOrder, boolean visible) {
        this.parentId = parentId;
        this.name = name;
        this.slug = slug;
        this.sortOrder = sortOrder;
        this.visible = visible;
    }

    public void update(String name, String slug, Long parentId, int sortOrder, boolean visible) {
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
        this.visible = visible;
    }
}
