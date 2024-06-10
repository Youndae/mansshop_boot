package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductOrderDSLRepository {

    Page<ProductOrder> findByUserId(MemberOrderDTO memberOrderDTO, OrderPageDTO pageDTO, Pageable pageable);
}
