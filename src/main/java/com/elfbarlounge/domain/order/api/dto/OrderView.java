package com.elfbarlounge.domain.order.api.dto;

import com.elfbarlounge.domain.order.domain.Order;
import com.elfbarlounge.domain.order.domain.OrderItem;
import com.elfbarlounge.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderView(
        Long id,
        String orderNo,
        OrderStatus status,
        long totalAmount,
        long productAmount,
        long shippingFee,
        long discountAmount,
        long pointUsed,
        long paidAmount,
        LocalDateTime orderedAt,
        String recipientName,
        String recipientPhoneMasked,
        String postalCode,
        String address1,
        String address2,
        String memo,
        List<ItemView> items
) {
    public static OrderView from(Order o) {
        return new OrderView(
                o.getId(), o.getOrderNo(), o.getStatus(),
                o.getTotalAmount(), o.getProductAmount(), o.getShippingFee(),
                o.getDiscountAmount(), o.getPointUsed(), o.getPaidAmount(),
                o.getOrderedAt(),
                o.getRecipientName(),
                maskPhone(o.getRecipientPhone()),
                o.getPostalCode(), o.getAddress1(), o.getAddress2(), o.getMemo(),
                o.getItems().stream().map(ItemView::from).toList()
        );
    }

    public record ItemView(
            Long id, Long productId, Long productOptionId, String productName, String optionText,
            long unitPrice, int quantity, long subtotal
    ) {
        public static ItemView from(OrderItem i) {
            return new ItemView(i.getId(), i.getProductId(), i.getProductOptionId(),
                    i.getProductName(), i.getOptionText(), i.getUnitPrice(), i.getQuantity(), i.getSubtotal());
        }
    }

    private static String maskPhone(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() < 8) return "***";
        return digits.substring(0, 3) + "-****-" + digits.substring(digits.length() - 4);
    }
}
