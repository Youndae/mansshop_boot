package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.business.AdminReviewDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminReviewDetailDTO;
import com.example.mansshop_boot.domain.dto.mypage.out.MyPageReviewDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.product.out.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductReviewDSLRepository {
    Page<ProductReviewDTO> findByProductId(String productId, Pageable pageable);

    Page<MyPageReviewDTO> findAllByUserId(String userId, Pageable pageable);

    List<AdminReviewDTO> findAllByAdminReviewList(AdminOrderPageDTO pageDTO, String listType);

    Long countByAdminReviewList(AdminOrderPageDTO pageDTO, String listType);

    AdminReviewDetailDTO findByAdminReviewDetail(long reviewId);
}
