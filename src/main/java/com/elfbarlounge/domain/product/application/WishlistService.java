package com.elfbarlounge.domain.product.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductRepository;
import com.elfbarlounge.domain.product.domain.WishlistItem;
import com.elfbarlounge.domain.product.domain.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long add(Long memberId, Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        if (p.getDeletedAt() != null) {
            throw ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다.");
        }
        return wishlistRepository.findByMemberIdAndProductId(memberId, productId)
                .map(WishlistItem::getId)
                .orElseGet(() -> wishlistRepository.save(
                        WishlistItem.builder().memberId(memberId).productId(productId).build()
                ).getId());
    }

    @Transactional
    public void remove(Long memberId, Long productId) {
        wishlistRepository.findByMemberIdAndProductId(memberId, productId)
                .ifPresent(wishlistRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<Long> productIds(Long memberId, Pageable pageable) {
        return wishlistRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(WishlistItem::getProductId);
    }

    @Transactional(readOnly = true)
    public long count(Long memberId) {
        return wishlistRepository.countByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public boolean contains(Long memberId, Long productId) {
        return wishlistRepository.existsByMemberIdAndProductId(memberId, productId);
    }
}
