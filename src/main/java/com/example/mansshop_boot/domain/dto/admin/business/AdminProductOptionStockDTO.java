package com.example.mansshop_boot.domain.dto.admin.business;

import com.example.mansshop_boot.domain.entity.ProductOption;

public record AdminProductOptionStockDTO(
        String size
        , String color
        , int optionStock
        , boolean optionIsOpen
) {

    public AdminProductOptionStockDTO(ProductOption productOption) {
        this(
                productOption.getSize()
                , productOption.getColor()
                , productOption.getStock()
                , productOption.isOpen()
        );
    }
}
