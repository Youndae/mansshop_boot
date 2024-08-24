package com.example.mansshop_boot.domain.dto.admin.business;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import lombok.Builder;

public record AdminOrderDetailDTO(
        String classification
        , String productName
        , String size
        , String color
        , int count
        , int price
        , boolean reviewStatus
) {

    @Builder
    public AdminOrderDetailDTO(ProductOrderDetail productOrderDetail) {
        this(
                productOrderDetail.getProduct().getClassification().getId()
                , productOrderDetail.getProduct().getProductName()
                , productOrderDetail.getProductOption().getSize()
                , productOrderDetail.getProductOption().getColor()
                , productOrderDetail.getOrderDetailCount()
                , productOrderDetail.getOrderDetailPrice()
                , productOrderDetail.isOrderReviewStatus()
        );
    }
}
