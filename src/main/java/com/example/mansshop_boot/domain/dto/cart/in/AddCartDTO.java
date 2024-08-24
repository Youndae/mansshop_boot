package com.example.mansshop_boot.domain.dto.cart.in;

import java.util.List;

public record AddCartDTO(
        Long optionId
        , int count
        , int price
        , List<AddCartDTO> addList
) {
}
