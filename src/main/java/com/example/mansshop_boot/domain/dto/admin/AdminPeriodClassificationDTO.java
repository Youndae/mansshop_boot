package com.example.mansshop_boot.domain.dto.admin;

public record AdminPeriodClassificationDTO(
        String classification
        , long classificationSales
        , long classificationSalesQuantity
) {
}
