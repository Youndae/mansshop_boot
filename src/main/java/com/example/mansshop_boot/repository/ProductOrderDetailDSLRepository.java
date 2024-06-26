package com.example.mansshop_boot.repository;


import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.MyPageOrderDetailDTO;
import com.example.mansshop_boot.domain.entity.ProductOrderDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderDetailDSLRepository {
    List<MyPageOrderDetailDTO> findByDetailList(List<Long> orderIdList);

    List<AdminBestSalesProductDTO> findPeriodBestProduct(LocalDateTime startDate, LocalDateTime endDate);

    List<AdminPeriodClassificationDTO> findPeriodClassification(LocalDateTime startDate, LocalDateTime endDate);

    AdminClassificationSalesDTO findPeriodClassificationSales(LocalDateTime startDate, LocalDateTime endDate, String classification);

    List<AdminClassificationSalesProductListDTO> findPeriodClassificationProductSales(LocalDateTime startDate, LocalDateTime endDate, String classification);

    List<ProductOrderDetail> findByOrderIds(List<Long> orderIdList);

    List<AdminProductSalesOptionDTO> getProductOptionSales(int year, int month, String productId);

}
