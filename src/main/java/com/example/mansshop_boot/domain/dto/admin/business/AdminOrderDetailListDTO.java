package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminOrderDetailListDTO(
        Long orderId,
        String classification,
        String productName,
        String size,
        String color,
        int count,
        int price,
        boolean reviewStatus
) {
}
