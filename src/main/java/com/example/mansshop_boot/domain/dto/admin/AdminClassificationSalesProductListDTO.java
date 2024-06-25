package com.example.mansshop_boot.domain.dto.admin;

public record AdminClassificationSalesProductListDTO(
        String productName
        , String size
        , String color
        , long productSales
        , long productQuantity
) {
}
