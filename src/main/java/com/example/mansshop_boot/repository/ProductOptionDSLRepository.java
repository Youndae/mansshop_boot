package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.product.ProductOptionDTO;

import java.util.List;

public interface ProductOptionDSLRepository {

    List<ProductOptionDTO> findByDetailOption(String productId);

    List<AdminProductOptionDTO> findAllByProductId(String productId);
}
