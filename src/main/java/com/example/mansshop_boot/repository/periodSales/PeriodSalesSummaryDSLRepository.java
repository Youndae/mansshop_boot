package com.example.mansshop_boot.repository.periodSales;

import com.example.mansshop_boot.domain.dto.admin.business.AdminBestSalesProductDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminClassificationSalesDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesListDTO;
import com.example.mansshop_boot.domain.dto.admin.business.AdminPeriodSalesStatisticsDTO;
import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PeriodSalesSummaryDSLRepository {

    List<AdminPeriodSalesListDTO> findPeriodList(int year);

    AdminPeriodSalesStatisticsDTO findPeriodStatistics(LocalDate startDate, LocalDate endDate);

    List<AdminPeriodSalesListDTO> findPeriodDailyList(LocalDate startDate, LocalDate endDate);

    AdminClassificationSalesDTO findDailySales(LocalDate period);

    PeriodSalesSummary findByPeriod(LocalDate period);
}
