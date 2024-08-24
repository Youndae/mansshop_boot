package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.entity.Product;

public record AdminDiscountResponseDTO(
        String productId
        , String classification
        , String productName
        , int price
        , int discount
        , int totalPrice
) {

    public AdminDiscountResponseDTO(Product product) {
        this(
                product.getId()
                , product.getClassification().getId()
                , product.getProductName()
                , product.getProductPrice()
                , product.getProductDiscount()
                , (int) (product.getProductPrice() * (1 - ((double)product.getProductDiscount() / 100)))
        );
    }
}
