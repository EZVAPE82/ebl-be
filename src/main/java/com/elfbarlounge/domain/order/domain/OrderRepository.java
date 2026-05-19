package com.elfbarlounge.domain.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndMemberId(Long id, Long memberId);
    Page<Order> findByMemberIdOrderByOrderedAtDesc(Long memberId, Pageable pageable);
    Page<Order> findByStatusOrderByOrderedAtDesc(OrderStatus status, Pageable pageable);
}
