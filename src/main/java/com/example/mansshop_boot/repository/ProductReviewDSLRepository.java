package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewDSLRepository {
    Page<ProductReviewDTO> findByProductId(String productId, Pageable pageable);
}
