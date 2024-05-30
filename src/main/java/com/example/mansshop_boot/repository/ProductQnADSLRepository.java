package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.product.ProductQnADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQnADSLRepository {
    Page<ProductQnADTO> findByProductId(String productId, Pageable pageable);
}
