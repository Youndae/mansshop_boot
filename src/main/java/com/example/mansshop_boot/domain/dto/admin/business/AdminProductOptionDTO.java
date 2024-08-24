package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminProductOptionDTO(
        Long optionId
        , String size
        , String color
        , int optionStock
        , boolean optionIsOpen
) {
}
