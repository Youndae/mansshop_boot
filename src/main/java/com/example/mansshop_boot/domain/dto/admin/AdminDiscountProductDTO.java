package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

@Builder
public record AdminDiscountProductDTO(
        String productId
        , String productName
        , int productPrice
) {
}
