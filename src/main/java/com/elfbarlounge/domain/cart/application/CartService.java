package com.elfbarlounge.domain.cart.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.cart.domain.Cart;
import com.elfbarlounge.domain.cart.domain.CartItem;
import com.elfbarlounge.domain.cart.domain.CartRepository;
import com.elfbarlounge.domain.product.domain.Product;
import com.elfbarlounge.domain.product.domain.ProductRepository;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart getOrCreate(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(Cart.builder().memberId(memberId).build()));
    }

    @Transactional
    public Cart addItem(Long memberId, Long productId, Long optionId, int quantity) {
        if (quantity <= 0) {
            throw ApiException.badRequest("CART_QUANTITY_INVALID", "수량은 1 이상이어야 합니다.");
        }
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> ApiException.notFound("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."));
        if (p.getStatus() != ProductStatus.ACTIVE) {
            throw ApiException.badRequest("PRODUCT_NOT_AVAILABLE", "구매할 수 없는 상품입니다.");
        }

        Cart cart = getOrCreate(memberId);
        cart.getItems().size(); // LAZY 초기화
        cart.findItem(productId, optionId).ifPresentOrElse(
                ci -> ci.addQuantity(quantity),
                () -> cart.addItem(CartItem.builder()
                        .productId(productId)
                        .productOptionId(optionId)
                        .quantity(quantity)
                        .build())
        );
        return cart;
    }

    @Transactional
    public Cart updateItem(Long memberId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw ApiException.badRequest("CART_QUANTITY_INVALID", "수량은 1 이상이어야 합니다.");
        }
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> ApiException.notFound("CART_NOT_FOUND", "장바구니를 찾을 수 없습니다."));
        cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .ifPresentOrElse(i -> i.updateQuantity(quantity),
                        () -> { throw ApiException.notFound("CART_ITEM_NOT_FOUND", "장바구니 항목이 없습니다."); });
        return cart;
    }

    @Transactional
    public void removeItem(Long memberId, Long cartItemId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> ApiException.notFound("CART_NOT_FOUND", "장바구니를 찾을 수 없습니다."));
        if (!cart.removeItemById(cartItemId)) {
            throw ApiException.notFound("CART_ITEM_NOT_FOUND", "장바구니 항목이 없습니다.");
        }
    }

    @Transactional
    public void clear(Long memberId) {
        cartRepository.findByMemberId(memberId).ifPresent(Cart::clear);
    }

    @Transactional(readOnly = true)
    public Cart view(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> Cart.builder().memberId(memberId).build());
        cart.getItems().size();
        return cart;
    }
}
