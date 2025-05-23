package com.example.mansshop_boot.domain.dto.admin.business;

import com.example.mansshop_boot.domain.entity.ProductOption;

public record AdminProductSalesOptionMonthDTO(
        long optionId
        , int month
        , String size
        , String color
        , long optionSales
        , long optionSalesQuantity
) {

    /*public AdminProductSalesOptionMonthDTO(ProductOption option, long optionSales, long optionSalesQuantity) {
        this(
                option.getId()
                , option.getSize()
                , option.getColor()
                , optionSales
                , optionSalesQuantity
        );
    }*/
}
