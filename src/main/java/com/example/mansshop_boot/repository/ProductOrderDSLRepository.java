package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.*;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderDSLRepository {

    Page<ProductOrder> findByUserId(MemberOrderDTO memberOrderDTO, OrderPageDTO pageDTO, Pageable pageable);

    Page<AdminOrderDTO> findAllOrderList(AdminOrderPageDTO pageDTO, Pageable pageable);

    Page<AdminOrderDTO> findAllNewOrderList(AdminOrderPageDTO pageDTO, LocalDateTime todayLastOrderTime, Pageable pageable);

    List<AdminPeriodSalesListDTO> findPeriodList(int year);

    AdminPeriodSalesStatisticsDTO findPeriodStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<AdminPeriodSalesListDTO> findPeriodDailyList(LocalDateTime startDate, LocalDateTime endDate);

    AdminClassificationSalesDTO findDailySales(LocalDateTime startDate, LocalDateTime endDate);

    Page<ProductOrder> findAllByDay(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<AdminProductSalesListDTO> getProductSalesList(AdminPageDTO pageDTO, Pageable pageable);

    AdminProductSalesDTO getProductSales(String productId);

    AdminSalesDTO getProductPeriodSales(int year, String productId);

    List<AdminPeriodSalesListDTO> getProductMonthPeriodSales(int year, String productId);



    List<AdminBestSalesProductDTO> findPeriodBestProductOrder(LocalDateTime startDate, LocalDateTime endDate);
}
