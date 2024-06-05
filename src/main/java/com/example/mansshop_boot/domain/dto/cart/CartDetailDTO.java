package com.example.mansshop_boot.domain.dto.cart;

public record CartDetailDTO(
        long cartDetailId
        , String productId
        , long optionId
        , String productName
        , String productThumbnail
        , String size
        , String color
        , int count
        , int price
) {
}
