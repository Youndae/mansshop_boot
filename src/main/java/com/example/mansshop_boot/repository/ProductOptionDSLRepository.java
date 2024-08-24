package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.business.AdminProductOptionDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductOptionDTO;
import com.example.mansshop_boot.domain.entity.ProductOption;

import java.util.List;

public interface ProductOptionDSLRepository {

    List<ProductOptionDTO> findByDetailOption(String productId);

    List<AdminProductOptionDTO> findAllByProductId(String productId);

    List<ProductOption> findAllOptionByProductIdList(List<String> productIdList);

    List<ProductOption> findAllOptionByProductId(String productId);
}
