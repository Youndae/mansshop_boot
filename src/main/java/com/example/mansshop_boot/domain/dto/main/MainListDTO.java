package com.example.mansshop_boot.domain.dto.main;



public record MainListDTO(
        String productId
        , String productName
        , String thumbnail
        , int productPrice
) {
}
