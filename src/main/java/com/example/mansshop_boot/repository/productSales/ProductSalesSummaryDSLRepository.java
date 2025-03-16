package com.example.mansshop_boot.repository.productSales;

import com.example.mansshop_boot.domain.dto.admin.business.*;
import com.example.mansshop_boot.domain.dto.admin.out.AdminProductSalesListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.entity.ProductSalesSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesSummaryDSLRepository {
    List<AdminBestSalesProductDTO> findPeriodBestProductOrder(LocalDate startDate, LocalDate endDate);

    List<AdminPeriodClassificationDTO> findPeriodClassification(LocalDate startDate, LocalDate endDate);

    AdminClassificationSalesDTO findPeriodClassificationSales(LocalDate startDate, LocalDate endDate, String classification);

    List<AdminClassificationSalesProductListDTO> findPeriodClassificationProductSales(LocalDate startDate, LocalDate endDate, String classification);

    Page<AdminProductSalesListDTO> findProductSalesList(AdminPageDTO pageDTO, Pageable pageable);

    AdminProductSalesDTO getProductSales(String productId);

    AdminSalesDTO getProductPeriodSales(int year, String productId);

    List<AdminPeriodSalesListDTO> getProductMonthPeriodSales(int year, String productId);

    List<AdminProductSalesOptionDTO> getProductOptionSales(int year, String productId);

    List<ProductSalesSummary> findAllByProductOptionIds(LocalDate periodMonth, List<Long> productOptionIds);
}
