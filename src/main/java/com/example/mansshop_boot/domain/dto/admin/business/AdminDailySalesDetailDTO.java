package com.example.mansshop_boot.domain.dto.admin.business;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;

public record AdminDailySalesDetailDTO(
        String productName,
        String size,
        String color,
        int count,
        int price
) {

    public AdminDailySalesDetailDTO(AdminOrderDetailListDTO orderDetail) {
        this(
                orderDetail.productName()
                , orderDetail.size()
                , orderDetail.color()
                , orderDetail.count()
                , orderDetail.price()
        );
    }
}
