package com.example.mansshop_boot.domain.dto.admin;

import java.util.List;

public record AdminClassificationSalesResponseDTO(
        String classification
        , long totalSales
        , long totalSalesQuantity
        , List<AdminClassificationSalesProductListDTO> product
) {

    public AdminClassificationSalesResponseDTO(String classification
                                            , AdminClassificationSalesDTO classificationSalesDTO
                                            , List<AdminClassificationSalesProductListDTO> product) {
        this(
                classification
                , classificationSalesDTO.sales()
                , classificationSalesDTO.salesQuantity()
                , product
        );
    }
}
