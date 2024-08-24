package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminPeriodClassificationDTO(
        String classification
        , long classificationSales
        , long classificationSalesQuantity
) {
}
