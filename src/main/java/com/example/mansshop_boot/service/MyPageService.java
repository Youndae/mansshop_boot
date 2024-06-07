package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.mypage.MemberOrderDTO;
import com.example.mansshop_boot.domain.dto.pageable.OrderPageDTO;
import org.springframework.http.ResponseEntity;


public interface MyPageService {

    ResponseEntity<?> getOrderList(OrderPageDTO orderPageDTO, MemberOrderDTO memberOrderDTO);
}
