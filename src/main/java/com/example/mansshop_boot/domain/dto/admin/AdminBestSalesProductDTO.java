package com.example.mansshop_boot.domain.dto.admin;

public record AdminBestSalesProductDTO(
        String productName
        , long productPeriodSalesQuantity
        , long productPeriodSales
) {
}
