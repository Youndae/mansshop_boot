package com.example.mansshop_boot.repository.periodSales;

import com.example.mansshop_boot.domain.entity.PeriodSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PeriodSalesSummaryRepository extends JpaRepository<PeriodSalesSummary, Long>, PeriodSalesSummaryDSLRepository {
}
