package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;

public interface OrderService {

    String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO);
}
