package com.example.mansshop_boot.domain.dto.admin.business;

import lombok.Builder;

@Builder
public record AdminPeriodSalesListDTO(
        int date
        , long sales
        , long salesQuantity
        , long orderQuantity
) {

    public AdminPeriodSalesListDTO(int date) {
        this(date, 0, 0, 0);
    }
}
