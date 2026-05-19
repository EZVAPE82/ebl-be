package com.elfbarlounge.domain.cart.domain;

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
import java.util.Optional;

@Getter
@Entity
@Table(name = "carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items = new ArrayList<>();

    @Builder
    private Cart(Long memberId) {
        this.memberId = memberId;
    }

    public Optional<CartItem> findItem(Long productId, Long optionId) {
        return items.stream()
                .filter(i -> i.getProductId().equals(productId)
                        && java.util.Objects.equals(i.getProductOptionId(), optionId))
                .findFirst();
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public boolean removeItemById(Long itemId) {
        return this.items.removeIf(i -> i.getId() != null && i.getId().equals(itemId));
    }

    public void clear() {
        this.items.clear();
    }
}
