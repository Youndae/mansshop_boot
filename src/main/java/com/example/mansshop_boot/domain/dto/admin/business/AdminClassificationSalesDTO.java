package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminClassificationSalesDTO(
        long sales
        , long salesQuantity
        , long orderQuantity
) {

    public static AdminClassificationSalesDTO emptyDTO() {
        return new AdminClassificationSalesDTO(0, 0, 0);
    }
}
