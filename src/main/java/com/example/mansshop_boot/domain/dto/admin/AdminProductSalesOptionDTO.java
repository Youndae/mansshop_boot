package com.example.mansshop_boot.domain.dto.admin;

public record AdminProductSalesOptionDTO(
        int month
        , long optionId
        , String size
        , String color
        , long optionSales
        , long optionSalesQuantity
) {
}
