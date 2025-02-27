package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminOptionStockDTO(
        String productId
        , String size
        , String color
        , int optionStock
        , boolean optionIsOpen
) {
}
