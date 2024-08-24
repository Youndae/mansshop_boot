package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminPeriodSalesStatisticsDTO(
        long monthSales
        , long monthSalesQuantity
        , long monthOrderQuantity
        , long monthDeliveryFee
        , long cashTotalPrice
        , long cartTotalPrice
) {
}
