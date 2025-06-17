package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.dto.admin.business.AdminClassificationSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminClassificationSalesProductListDTO;

import java.util.List;

public record AdminClassificationSalesResponseDTO(
        String classification,
        long totalSales,
        long totalSalesQuantity,
        List<AdminClassificationSalesProductListDTO> productList
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
