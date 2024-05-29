package com.example.mansshop_boot.domain.dto.product;

public record ProductOptionDTO(
        Long optionId
        , String size
        , String color
        , int stock
) {
}
