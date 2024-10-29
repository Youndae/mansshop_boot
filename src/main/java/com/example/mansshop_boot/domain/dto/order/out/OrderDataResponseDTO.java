package com.example.mansshop_boot.domain.dto.order.out;

import com.example.mansshop_boot.domain.dto.order.business.OrderDataDTO;

import java.util.List;

public record OrderDataResponseDTO(
        List<OrderDataDTO> orderData
        , int totalPrice
) {
}
