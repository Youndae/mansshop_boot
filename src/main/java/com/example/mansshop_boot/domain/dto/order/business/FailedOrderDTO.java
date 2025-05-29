package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;

import java.time.LocalDateTime;

public record FailedOrderDTO(
        PaymentDTO paymentDTO,
        CartMemberDTO cartMemberDTO,
        LocalDateTime failedTime,
        String message
) {
}
