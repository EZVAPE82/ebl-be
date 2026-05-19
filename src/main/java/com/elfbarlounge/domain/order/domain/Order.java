package com.elfbarlounge.domain.order.domain;

import com.elfbarlounge.common.domain.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", length = 32, nullable = false, unique = true)
    private String orderNo;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    @Column(name = "product_amount", nullable = false)
    private long productAmount;

    @Column(name = "shipping_fee", nullable = false)
    private long shippingFee;

    @Column(name = "discount_amount", nullable = false)
    private long discountAmount;

    @Column(name = "point_used", nullable = false)
    private long pointUsed;

    @Column(name = "paid_amount", nullable = false)
    private long paidAmount;

    @Column(name = "channel", length = 16, nullable = false)
    private String channel;

    @Column(name = "channel_order_id", length = 64)
    private String channelOrderId;

    @Column(name = "member_coupon_id")
    private Long memberCouponId;

    @Column(name = "recipient_name", length = 80)
    private String recipientName;

    @Column(name = "recipient_phone", length = 32)
    private String recipientPhone;

    @Column(name = "postal_code", length = 16)
    private String postalCode;

    @Column(name = "address1", length = 200)
    private String address1;

    @Column(name = "address2", length = 200)
    private String address2;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    private Order(String orderNo, Long memberId, String channel, String channelOrderId,
                  long productAmount, long shippingFee, long discountAmount, long pointUsed,
                  Long memberCouponId,
                  String recipientName, String recipientPhone, String postalCode,
                  String address1, String address2, String memo) {
        this.orderNo = orderNo;
        this.memberId = memberId;
        this.channel = channel != null ? channel : "SELF";
        this.channelOrderId = channelOrderId;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.productAmount = productAmount;
        this.shippingFee = shippingFee;
        this.discountAmount = discountAmount;
        this.pointUsed = pointUsed;
        this.totalAmount = productAmount + shippingFee;
        this.paidAmount = Math.max(0, productAmount + shippingFee - discountAmount - pointUsed);
        this.memberCouponId = memberCouponId;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.memo = memo;
        this.orderedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void markPreparing() {
        this.status = OrderStatus.PREPARING;
    }

    public void markShipping() {
        this.status = OrderStatus.SHIPPING;
    }

    public void markDelivered() {
        this.status = OrderStatus.DELIVERED;
    }

    public void markCanceled() {
        this.status = OrderStatus.CANCELED;
    }

    public void markRefunded() {
        this.status = OrderStatus.REFUNDED;
    }
}
