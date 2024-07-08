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
        , int discount
) {
    public CartDetailDTO(long cartDetailId
                        , String productId
                        , long optionId
                        , String productName
                        , String productThumbnail
                        , String size
                        , String color
                        , int count
                        , int price
                        , int discount) {
        this.cartDetailId = cartDetailId;
        this.productId = productId;
        this.optionId = optionId;
        this.productName = productName;
        this.productThumbnail = productThumbnail;
        this.size = size;
        this.color = color;
        this.count = count;
        this.price = (int) (price * (1 - ((double) discount / 100)));
        this.discount = discount;
    }
}
