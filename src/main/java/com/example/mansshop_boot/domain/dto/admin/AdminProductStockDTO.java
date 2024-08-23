package com.example.mansshop_boot.domain.dto.admin;


import java.util.List;

public record AdminProductStockDTO(
        String productId
        , String classification
        , String productName
        , int totalStock
        , boolean isOpen
        , List<AdminProductOptionStockDTO> optionList
) {

    public AdminProductStockDTO(String productId, AdminProductStockDataDTO dto, List<AdminProductOptionStockDTO> optionList) {
        this(
                productId
                , dto.classification()
                , dto.productName()
                , dto.totalStock()
                , dto.isOpen()
                , optionList
        );
    }
}
