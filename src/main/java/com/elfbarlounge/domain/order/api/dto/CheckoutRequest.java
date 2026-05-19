package com.elfbarlounge.domain.order.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        Long memberCouponId,
        @PositiveOrZero long pointUsed,

        @NotBlank @Size(max = 80) String recipientName,
        @NotBlank @Size(max = 32) String recipientPhone,
        @NotBlank @Size(max = 16) String postalCode,
        @NotBlank @Size(max = 200) String address1,
        @Size(max = 200) String address2,
        @Size(max = 200) String memo,

        // PG token (실연동 시): 클라이언트에서 PG SDK로 발급받은 결제 토큰
        @NotBlank @Size(max = 200) String paymentToken,
        @NotNull @Size(max = 20) String paymentMethod
) {
}
