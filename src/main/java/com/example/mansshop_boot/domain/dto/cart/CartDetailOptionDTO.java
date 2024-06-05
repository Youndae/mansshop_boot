package com.example.mansshop_boot.domain.dto.cart;

import lombok.Builder;

@Builder
public record CartDetailOptionDTO(
        long cartDetailId
        , String size
        , String color
        , int count
        , int price
) {
}
