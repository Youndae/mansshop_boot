package com.example.mansshop_boot.domain.dto.admin;

public record AdminDailySalesDetailDTO(
        String productName
        , String size
        , String color
        , int count
        , int price
) {
}
