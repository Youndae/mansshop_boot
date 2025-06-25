package com.example.mansshop_boot.domain.dto.order.business;

public record OrderProductInfoDTO(
        String productId,
        long optionId,
        String productName,
        String size,
        String color,
        int price
){
    public OrderProductInfoDTO(String productId,
                            long optionId,
                            String productName,
                            String size,
                            String color,
                            int price,
                            int discount) {
        this(
                productId,
                optionId,
                productName,
                size,
                color,
                (price - (price * discount / 100))
        );
    }
}
