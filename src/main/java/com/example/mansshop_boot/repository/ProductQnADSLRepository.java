package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.ProductQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.product.ProductQnADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQnADSLRepository {
    Page<ProductQnADTO> findByProductId(String productId, Pageable pageable);

    Page<ProductQnAListDTO> findByUserId(String userId, Pageable pageable);

    MyPageProductQnADTO findByQnAId(long productQnAId);

    Page<AdminQnAListResponseDTO> findAllByAdminProductQnA(AdminOrderPageDTO pageDTO, Pageable pageable);
}
