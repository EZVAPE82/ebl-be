package com.elfbarlounge.domain.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * orderItemId가 해당 회원의 주문에 속하는지 확인 + Order/Item 정보 fetch.
     */
    @Query("""
        SELECT i FROM OrderItem i
         WHERE i.id = :itemId
           AND EXISTS (
               SELECT 1 FROM Order o
                WHERE o.id = i.orderId
                  AND o.memberId = :memberId
           )
        """)
    Optional<OrderItem> findByIdAndMemberId(@Param("itemId") Long itemId,
                                            @Param("memberId") Long memberId);
}
