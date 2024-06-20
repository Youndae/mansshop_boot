package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

public record AdminDiscountResponseDTO(
        String productId
        , String classification
        , String productName
        , int price
        , int discount
        , int totalPrice
) {

    @Builder
    public AdminDiscountResponseDTO(String productId, String classification, String productName, int price, int discount) {
        this(
                productId
                , classification
                , productName
                , price
                , discount
                , (int) (price * (1 - ((double)discount / 100)))
        );
    }
}
