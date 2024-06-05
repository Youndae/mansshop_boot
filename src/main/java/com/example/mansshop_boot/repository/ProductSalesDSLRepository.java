package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.entity.ProductSales;

import java.util.List;

public interface ProductSalesDSLRepository {

    List<ProductSales> findAllByOptionIds(List<Long> orderOptionIdList);

}
