package com.example.mansshop_boot.domain.dto.admin.out;

import lombok.Builder;

@Builder
public record AdminDiscountProductDTO(
        String productId
        , String productName
        , int productPrice
) {
}
