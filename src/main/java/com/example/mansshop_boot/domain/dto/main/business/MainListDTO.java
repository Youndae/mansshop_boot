package com.example.mansshop_boot.domain.dto.main.business;



public record MainListDTO(
        String productId,
        String productName,
        String thumbnail,
        int price,
        int discount,
        long stock
) {
}
