package com.example.mansshop_boot.domain.dto.order.business;

import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;
import com.example.mansshop_boot.domain.entity.ProductOrder;

import java.time.LocalDate;

public record PeriodSummaryQueueDTO(
        LocalDate period,
        long sales,
        long salesQuantity,
        long totalDeliveryFee,
        long cashSales,
        long cardSales
) {
    public PeriodSummaryQueueDTO(ProductOrder productOrder) {
        this(
                LocalDate.now(),
                productOrder.getOrderTotalPrice(),
                productOrder.getProductCount(),
                productOrder.getDeliveryFee(),
                productOrder.getPaymentType().equals("cash") ? productOrder.getOrderTotalPrice() : 0L,
                productOrder.getPaymentType().equals("card") ? productOrder.getOrderTotalPrice() : 0L
        );
    }

    public PeriodSalesSummary toEntity() {
        return PeriodSalesSummary.builder()
                .period(this.period)
                .sales(this.sales)
                .salesQuantity(this.salesQuantity)
                .orderQuantity(1L)
                .totalDeliveryFee(this.totalDeliveryFee)
                .cashTotal(cashSales)
                .cardTotal(cardSales)
                .build();
    }
}
