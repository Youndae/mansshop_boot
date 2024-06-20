package com.example.mansshop_boot.repository;

import com.example.mansshop_boot.domain.dto.admin.AdminOrderDTO;
import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminPageDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ProductOrderDSLRepository {

    Page<ProductOrder> findByUserId(MemberOrderDTO memberOrderDTO, OrderPageDTO pageDTO, Pageable pageable);

    Page<AdminOrderDTO> findAllOrderList(AdminOrderPageDTO pageDTO, Pageable pageable);

    Page<AdminOrderDTO> findAllNewOrderList(AdminOrderPageDTO pageDTO, LocalDateTime todayLastOrderTime, Pageable pageable);
}
