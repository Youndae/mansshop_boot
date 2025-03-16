package com.example.mansshop_boot.repository.productSales;

import com.example.mansshop_boot.domain.entity.ProductSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSalesSummaryRepository extends JpaRepository<ProductSalesSummary, Long>, ProductSalesSummaryDSLRepository {
}
