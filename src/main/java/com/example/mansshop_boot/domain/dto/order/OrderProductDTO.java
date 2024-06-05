package com.example.mansshop_boot.domain.dto.order;

public record OrderProductDTO(
        long optionId
        , String productId
        , int detailCount
        , int detailPrice
) {
}
