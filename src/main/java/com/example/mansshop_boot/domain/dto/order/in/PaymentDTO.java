package com.example.mansshop_boot.domain.dto.order.in;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumuration.OrderStatus;

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
        , int productCount
) {

    public ProductOrder toOrderEntity(String uid) {
        return ProductOrder.builder()
                .member(
                        Member.builder()
                                .userId(uid)
                                .build()
                )
                .recipient(recipient)
                .orderPhone(phone)
                .orderAddress(address)
                .orderMemo(orderMemo)
                .orderTotalPrice(totalPrice)
                .deliveryFee(deliveryFee)
                .paymentType(paymentType)
                .orderStat(OrderStatus.ORDER.getStatusStr())
                .build();
    }
}
