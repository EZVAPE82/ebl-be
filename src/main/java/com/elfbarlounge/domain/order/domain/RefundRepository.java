package com.elfbarlounge.domain.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
