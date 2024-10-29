package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;

import java.util.List;

public interface OrderService {

    String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO);

    OrderDataResponseDTO getProductOrderData(List<OrderProductRequestDTO> requestDTO);

    OrderDataResponseDTO getCartOrderData(List<Long> cartDetailIds, CartMemberDTO cartMemberDTO);
}
