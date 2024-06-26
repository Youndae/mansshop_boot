package com.example.mansshop_boot.domain.dto.admin;

public record AdminProductSalesDTO(
        String productName
        , long totalSales
        , long totalSalesQuantity
) {
}
