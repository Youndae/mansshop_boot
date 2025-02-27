package com.example.mansshop_boot.domain.dto.admin.business;

public record AdminProductOptionStockDTO(
        String size
        , String color
        , int optionStock
        , boolean optionIsOpen
) {

    public AdminProductOptionStockDTO(AdminOptionStockDTO productOption) {
        this(
                productOption.size()
                , productOption.color()
                , productOption.optionStock()
                , productOption.optionIsOpen()
        );
    }
}
