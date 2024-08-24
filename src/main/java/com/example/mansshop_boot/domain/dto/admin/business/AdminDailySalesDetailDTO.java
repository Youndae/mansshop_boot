package com.example.mansshop_boot.domain.dto.admin.business;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;

public record AdminDailySalesDetailDTO(
        String productName
        , String size
        , String color
        , int count
        , int price
) {

    public AdminDailySalesDetailDTO(ProductOrderDetail orderDetail) {
        this(
                orderDetail.getProduct().getProductName()
                , orderDetail.getProductOption().getSize()
                , orderDetail.getProductOption().getColor()
                , orderDetail.getOrderDetailCount()
                , orderDetail.getOrderDetailPrice()
        );
    }
}
