package com.example.mansshop_boot.domain.dto.admin;

public record AdminProductSalesOptionDTO(
        long optionId
        , String size
        , String color
        , long optionSales
        , long optionSalesQuantity
) {
}
