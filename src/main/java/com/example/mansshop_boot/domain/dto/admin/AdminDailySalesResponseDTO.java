package com.example.mansshop_boot.domain.dto.admin;

import java.util.List;

public record AdminDailySalesResponseDTO(
        long totalPrice
        , long deliveryFee
        , String paymentType
        , List<AdminDailySalesDetailDTO> detailList
) {
}
