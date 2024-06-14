package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MyPageReviewDTO;
import com.example.mansshop_boot.domain.dto.pageable.ProductDetailPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewDSLRepository {
    Page<ProductReviewDTO> findByProductId(String productId, Pageable pageable);

    Page<MyPageReviewDTO> findAllByUserId(String userId, Pageable pageable);
}
