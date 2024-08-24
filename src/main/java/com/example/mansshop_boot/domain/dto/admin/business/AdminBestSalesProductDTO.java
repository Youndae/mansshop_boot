package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminBestSalesProductDTO(
        String productName
        , long productPeriodSalesQuantity
        , long productPeriodSales
) {
}
