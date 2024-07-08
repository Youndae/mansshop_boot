package com.example.mansshop_boot.domain.dto.order.in;

import com.example.mansshop_boot.domain.entity.ProductOrderDetail;
import com.example.mansshop_boot.domain.entity.Product;
import com.example.mansshop_boot.domain.entity.ProductOption;

public record OrderProductDTO(
        long optionId
        , String productName
        , String productId
        , int detailCount
        , int detailPrice
) {
    public ProductOrderDetail toOrderDetailEntity() {
        return ProductOrderDetail.builder()
                .productOption(
                        ProductOption.builder()
                                .id(optionId)
                                .build()
                )
                .product(
                        Product.builder()
                                .id(productId)
                                .build()
                )
                .orderDetailCount(detailCount)
                .orderDetailPrice(detailPrice)
                .build();
    }
}
