package com.example.mansshop_boot.domain.dto.admin;

public record AdminProductStockDataDTO(
        String productId
        , String classification
        , String productName
        , int totalStock
        , boolean isOpen
) {
}
