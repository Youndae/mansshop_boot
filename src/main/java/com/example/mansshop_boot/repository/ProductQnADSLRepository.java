package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.business.MyPageProductQnADTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.out.ProductQnAListDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.product.business.ProductQnADTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductQnADSLRepository {
    Page<ProductQnADTO> findByProductId(String productId, Pageable pageable);

    Page<ProductQnAListDTO> findByUserId(String userId, Pageable pageable);

    MyPageProductQnADTO findByQnAId(long productQnAId);

    List<AdminQnAListResponseDTO> findAllByAdminProductQnA(AdminOrderPageDTO pageDTO);

    Long findAllByAdminProductQnACount(AdminOrderPageDTO pageDTO);
}
