package com.example.mansshop_boot.domain.dto.cart.out;

public record CartDetailDTO(
        long cartDetailId,
        String productId,
        long optionId,
        String productName,
        String productThumbnail,
        String size,
        String color,
        int count,
        int originPrice,
        int price,
        int discount
) {
    public CartDetailDTO(long cartDetailId,
                        String productId,
                        long optionId,
                        String productName,
                        String productThumbnail,
                        String size,
                        String color,
                        int count,
                        int price,
                        int discount) {
        this(
                cartDetailId,
                productId,
                optionId,
                productName,
                productThumbnail,
                size,
                color,
                count,
                price * count,
                (price - (price * discount / 100)) * count,
                discount
        );
    }
}
