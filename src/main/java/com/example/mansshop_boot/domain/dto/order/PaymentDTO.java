package com.example.mansshop_boot.domain.dto.order;

import java.util.List;

public record PaymentDTO(
        String recipient
        , String phone
        , String orderMemo
        , String address
        , List<OrderProductDTO> orderProduct
        , int deliveryFee
        , int totalPrice
        , String paymentType
        , String orderType
) {
}
