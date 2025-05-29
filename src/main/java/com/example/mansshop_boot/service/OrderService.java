package com.example.mansshop_boot.service;

import com.example.mansshop_boot.domain.dto.cart.business.CartMemberDTO;
import com.example.mansshop_boot.domain.dto.order.business.FailedOrderDTO;
import com.example.mansshop_boot.domain.dto.order.business.ProductOrderDataDTO;
import com.example.mansshop_boot.domain.dto.order.in.OrderProductRequestDTO;
import com.example.mansshop_boot.domain.dto.order.in.PaymentDTO;
import com.example.mansshop_boot.domain.dto.order.out.OrderDataResponseDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;

import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.FallbackMapKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;
import java.util.List;

public interface OrderService {

    String payment(PaymentDTO paymentDTO, CartMemberDTO cartMemberDTO);

    OrderDataResponseDTO getProductOrderData(List<OrderProductRequestDTO> requestDTO, HttpServletRequest request, HttpServletResponse response, Principal principal);

    OrderDataResponseDTO getCartOrderData(List<Long> cartDetailIds, CartMemberDTO cartMemberDTO, HttpServletRequest request, HttpServletResponse response);

	ResponseMessageDTO validateOrder(OrderDataResponseDTO requestDTO, Principal principal, HttpServletRequest request, HttpServletResponse response);

    String retryFailedOrder(FailedOrderDTO failedOrderDTO, FallbackMapKey fallbackMapKey);
}
