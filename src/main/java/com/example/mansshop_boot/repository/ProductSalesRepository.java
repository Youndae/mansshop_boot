package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSalesRepository extends JpaRepository<ProductSales, Long>, ProductSalesDSLRepository{

}
