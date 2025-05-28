package com.example.mansshop_boot.domain.dto.order.in;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.ProductOrder;
import com.example.mansshop_boot.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "주문 요청 데이터")
public record PaymentDTO(
        @Schema(name = "recipient", description = "수령인")
        @NotNull(message = "수령인은 필수 데이터입니다.")
        String recipient,
        @Schema(name = "phone", description = "수령인 연락처")
        @NotNull(message = "연락처는 필수 데이터입니다.")
        String phone,
        @Schema(name = "orderMemo", description = "주문 메모")
        String orderMemo,
        @Schema(name = "address", description = "배송지")
        @NotNull(message = "배송지는 필수 데이터입니다.")
        String address,
        @Schema(name = "orderProduct", description = "주문 상품 정보", type = "array")
        List<OrderProductDTO> orderProduct,
        @Schema(name = "deliveryFee", description = "배송비")
        int deliveryFee,
        @Schema(name = "totalPrice", description = "결제 금액")
        int totalPrice,
        @Schema(name = "paymentType", description = "결제 타입(card, cash)")
        @NotNull(message = "결제 타입은 필수 데이터입니다.")
        String paymentType,
        @Schema(name = "orderType", description = "주문 요청 방식(cart, direct)")
        @NotNull(message = "주문 요청 방식은 필수 데이터입니다.")
        String orderType,
        @Schema(name = "productCount", description = "상품 개수")
        @NotNull(message = "상품 수량은 필수 데이터입니다.")
        int productCount
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
                .createdAt(LocalDateTime.now())
                .build();
    }
}
