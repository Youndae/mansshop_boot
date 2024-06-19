package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

@Builder
public record AdminProductOptionStockDTO(
        String size
        , String color
        , int optionStock
        , boolean optionIsOpen
) {
}
