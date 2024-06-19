package com.example.mansshop_boot.domain.dto.admin;

public record AdminProductListDTO(
        String productId
        , String classification
        , String productName
        , int stock
        , Long optionCount
        , int price
) {
}
