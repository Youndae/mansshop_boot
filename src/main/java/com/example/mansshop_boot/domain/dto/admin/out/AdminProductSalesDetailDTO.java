package com.example.mansshop_boot.domain.dto.admin.out;

import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminProductSalesOptionDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminSalesDTO;

import java.util.Collections;
import java.util.List;

public record AdminProductSalesDetailDTO(
        String productName,
        long totalSales,
        long totalSalesQuantity,
        long yearSales,
        long yearSalesQuantity,
        long lastYearComparison,
        long lastYearSales,
        long lastYearSalesQuantity,
        List<AdminPeriodSalesListDTO> monthSales,
        List<AdminProductSalesOptionDTO> optionTotalSales,
        List<AdminProductSalesOptionDTO> optionYearSales,
        List<AdminProductSalesOptionDTO> optionLastYearSales

) {

    public AdminProductSalesDetailDTO(AdminProductSalesDTO totalSalesDTO
                                    , AdminSalesDTO yearSalesDTO
                                    , AdminSalesDTO lastYearSalesDTO
                                    , List<AdminPeriodSalesListDTO> monthSales
                                    , List<AdminProductSalesOptionDTO> optionTotalSales
                                    , List<AdminProductSalesOptionDTO> optionYearSales
                                    , List<AdminProductSalesOptionDTO> optionLastYearSales){
        this(
                totalSalesDTO.productName()
                , totalSalesDTO.totalSales()
                , totalSalesDTO.totalSalesQuantity()
                , yearSalesDTO.sales()
                , yearSalesDTO.salesQuantity()
                , yearSalesDTO.sales() - lastYearSalesDTO.sales()
                , lastYearSalesDTO.sales()
                , lastYearSalesDTO.salesQuantity()
                , monthSales
                , optionTotalSales
                , optionYearSales
                , optionLastYearSales
        );
    }

    public static AdminProductSalesDetailDTO emptyDTO() {
        return new AdminProductSalesDetailDTO(
                null,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
