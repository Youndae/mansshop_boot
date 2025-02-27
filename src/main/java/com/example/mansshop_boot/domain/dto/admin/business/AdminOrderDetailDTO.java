package com.example.mansshop_boot.domain.dto.admin.business;


public record AdminOrderDetailDTO(
        String classification
        , String productName
        , String size
        , String color
        , int count
        , int price
        , boolean reviewStatus
) {

    public AdminOrderDetailDTO(AdminOrderDetailListDTO detail) {
        this(
                detail.classification(),
                detail.productName(),
                detail.size(),
                detail.color(),
                detail.count(),
                detail.price(),
                detail.reviewStatus()
        );
    }
}
