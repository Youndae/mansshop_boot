package com.example.mansshop_boot.domain.dto.admin;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminProductStockDTO(
        String productId
        , String classification
        , String productName
        , int totalStock
        , boolean isOpen
        , List<AdminProductOptionStockDTO> optionList
) {
}
