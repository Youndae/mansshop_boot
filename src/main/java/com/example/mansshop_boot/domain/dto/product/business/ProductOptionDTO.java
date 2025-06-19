package com.example.mansshop_boot.domain.dto.product.business;

public record ProductOptionDTO(
        Long optionId,
        String size,
        String color,
        int stock
) {
}
