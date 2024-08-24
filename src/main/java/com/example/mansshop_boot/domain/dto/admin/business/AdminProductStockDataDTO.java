package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminProductStockDataDTO(
        String productId
        , String classification
        , String productName
        , int totalStock
        , boolean isOpen
) {
}
