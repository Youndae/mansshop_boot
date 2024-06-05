package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.PeriodSales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodSalesRepository extends JpaRepository<PeriodSales, Long>, PeriodSalesDSLRepository {

}
