package com.elfbarlounge.domain.order.application;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderRepository;
import com.elfbarlounge.domain.order.domain.Shipment;
import com.elfbarlounge.domain.order.domain.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Shipment ship(Long orderId, String courier, String trackingNo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));

        Shipment ship = shipmentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseGet(() -> shipmentRepository.save(Shipment.builder().orderId(orderId).build()));
        ship.ship(courier, trackingNo);
        order.markShipping();
        return ship;
    }

    @Transactional
    public void markDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."));
        Shipment ship = shipmentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> ApiException.notFound("SHIPMENT_NOT_FOUND", "배송 정보를 찾을 수 없습니다."));
        ship.markDelivered();
        order.markDelivered();
    }
}
