package com.example.mansshop_boot.domain.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderDTO(
        long orderId
        , String recipient
        , String userId
        , String phone
        , LocalDateTime createdAt
        , String address
        , int orderStatus
) {
    public AdminOrderResponseDTO toResponseDTO(List<AdminOrderDetailDTO> detailList){
        return AdminOrderResponseDTO.builder()
                .orderId(orderId)
                .recipient(recipient)
                .userId(userId)
                .phone(phone)
                .createdAt(createdAt)
                .address(address)
                .orderStatus(orderStatus)
                .detailList(detailList)
                .build();
    }
}
