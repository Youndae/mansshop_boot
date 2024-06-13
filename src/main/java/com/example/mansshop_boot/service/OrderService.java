package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.PaymentDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {

    String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO);
}
